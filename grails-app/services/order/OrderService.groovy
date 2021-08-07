package order

import base.InvalidParameterException
import goods.GoodsBagService
import goods.GoodsService
import mongo.MongoService
import org.apache.commons.lang3.time.DateUtils
import shareshopping.Code
import shareshopping.DateTools
import user.UserCouponService
import user.UserService

import java.text.DecimalFormat

class OrderService  extends MongoService{
    static int payExpirationHours=3
    def userAddressService
    GoodsService goodsService
    OrderNumberService orderNumberService
    UserCouponService userCouponService
    UserService userService
    OrderActivityService orderActivityService
    GoodsBagService goodsBagService
    def statusNameMap=[
            DONG:"配货中",WAIT_PAY:"待支付","DELIVERY":"配送中",
            WAIT_CONFIRM:"待确认",COMPLETED:"已完成",RETURNED:"已退货",
            RETURN_DOING:"退货处理中",CANCELED:"已取消"
    ]
    def fromStatusChangeMap=[
            DONG:["WAIT_PAY"],
            DELIVERY:["DONG"],
            WAIT_CONFIRM:["DELIVERY"],
            COMPLETED:["WAIT_CONFIRM"],
            RETURN_DOING:["DONG","DELIVERY","WAIT_CONFIRM"],
            RETURNED:["RETURN_DOING"],
            CANCELED:["WAIT_PAY"]
    ]
    def userShowChangeButton=[
            WAIT_PAY:[[id:"CANCELED",name:"取消"],[id:"PAY",name:"付款"]],
            DONG:[[id:"RETURN_DOING",name:"退款"]],
            DELIVERY:[[id:"RETURN_DOING",name:"退款"]],
            WAIT_CONFIRM:[[id:"RETURN_DOING",name:"退款"],[id:"COMPLETED",name:"确认收货"]],
            COMPLETED:[],
            RETURN_DOING:[],
            RETURNED:[],
            CANCELED:[]
    ]
    def managerShowChangeButton=[
            WAIT_PAY:[],
            DONG:[],
            DELIVERY:[[id:"WAIT_CONFIRM",name:"已送达"]],
            WAIT_CONFIRM:[],
            COMPLETED:[],
            RETURN_DOING:[[id:"RETURNED",name:"已退货"]],
            RETURNED:[],
            CANCELED:[]
    ]
    @Override
    String collectionName() {
        "order"
    }
    def create(token,map){
        //  map->      addressId:addressId,couponId:coupon.id,goods:confirmToService,remark:remark
        def result=this.checkParams(token,map)
        if(result.code!=200){
            return result
        }
        def order=[token:token,status:"WAIT_PAY",strStatus:statusNameMap.WAIT_PAY,code:orderNumberService.create()]
        def userAddress=userAddressService.findById(map.addressId)
        order.putAll(userAddress.subMap(["country","strCountry","villager","strVillager","name","phone"]))
        order.address=userAddress.detail
        def sum=0
        def totalGoodsCount=0
        def goods=[]
        for(def item:map.goods){
            def goodsDetail=goodsService.findById(item.id)
            if(!goodsDetail||goodsDetail.status!="ENABLE"){ //不存在，商品已下架
                result.code= Code.goodsDelete
                result.message="${goodsDetail.name} 已下架"
                //还原库存
                goodsService.recoverGoodsNumber(goods)
                return result
            }
            def reduceNumber=goodsService.reduceNumber(item.id,item.count)
            if(!reduceNumber){ //库存不足
                result.code= Code.goodsEmpty
                result.message="${goodsDetail.name} 库存不足,商品余量为${goodsDetail.number}"
                //还原库存
                goodsService.recoverGoodsNumber(goods)
                return result
            }
            def saveMap=goodsDetail.subMap(["id","name","indexImage","sum","oldSum","nature","strNature","category","detailFileList","remark"])
            saveMap.buyCount=item.count
            totalGoodsCount+=item.count
            goods<<saveMap
            sum+=goodsDetail.sum*item.count
        }
        order.goods=goods
        order.totalGoodsCount=totalGoodsCount
        order.sum=sum
        order.realSum=sum
        //处理优惠券业务
        if(map.couponId){
            def coupon=userCouponService.useCoupon(token,map.couponId)
            if(!coupon){
                result.code= Code.couponNotFound
                result.message="无可用优惠券"
                goodsService.recoverGoodsNumber(goods)
                return result
            }
            /*if(coupon.sum>sum){
                result.code= Code.couponGtSum
                result.message="优惠券金额大于商品金额"
                goodsService.recoverGoodsNumber(goods)
                return result
            }*/
            order.coupon=[id:coupon.id,name:coupon.name,sum:coupon.sum,type:coupon.type]
            def realSum=sum-coupon.sum
            BigDecimal bg = new BigDecimal(realSum);
            realSum = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if(realSum<0){
                realSum=0
            }
            order.realSum=realSum
        }
        order.payExpirationTime=DateUtils.addHours(new Date(),payExpirationHours)
        order=this.save(order)
        orderActivityService.addActivity(order.id)
        result.data=order
        //去掉购物车里面相关商品
        goodsBagService.delete([token:token,goodsId:['$in':goods*.id]])
        return result
    }
    def paySuccess(token,orderId){
        def toUpdate=[status:"DONG",strStatus:statusNameMap.DONG,payTime:new Date()]
        def order=this.updateOne([id:orderId,status:"WAIT_PAY"],toUpdate)
        return order
    }
    def orderCancel(orderId){
        def toUpdate=[status:"CANCELED",strStatus:statusNameMap.CANCELED,cancelTime:new Date()]
        def order=this.updateOne([id:orderId,status:"WAIT_PAY"],toUpdate)
        if(order){
            //还原优惠券
            if(order.coupon){
                userCouponService.recoverCoupon(order.token,order.coupon.id)
            }
            //还原库存
            goodsService.recoverGoodsNumber(order.goods)
        }
        orderActivityService.addActivity(order.id)
        return order
    }
    def updateStatus(token,map){
        def result=[code:200]
        def orderId=map.orderId
        def toStatus=map.toStatus
        def order=this.updateOne([id:orderId,status:['$in':fromStatusChangeMap[toStatus]]],[status:toStatus,strStatus: statusNameMap[toStatus]])
        if(!order){
            result.code=Code.orderStatusChangeError
            result.message="订单状态错误，请刷新数据后重新操作"
            return result
        }
        if(toStatus=="COMPLETED"){ //给用户添加积分
            def score=order.realSum
            userService.addScore(token,score)
        }
        orderActivityService.addActivity(order.id)
        result.data=order
        return result
    }
    def checkParams(token,map){
        //map.goods=[[id,name,count]]
        def result=[code:200]
        if(!map.addressId||!map.goods){
            throw new InvalidParameterException("addressId and goods can not be null!")
        }
        return result
    }
    def list(params){
        def pageNumber=params.int("pageNumber",1)
        def pageSize=params.int("pageSize",10)
        def filter=[:]
        if(params.startTime&&params.endTime){
            filter.'$and'=[[dateCreated:['$gte': DateTools.parseDate(params.startDate)]], [dateCreated:['$lte':DateTools.parseDate(params.endDate)]]]
        }
        return this.findAll(filter,(pageNumber-1)*pageSize,pageSize,[dateCreated:-1])
    }
    def userOrderList(token,params){
        def pageNumber=params.int("pageNumber",1)
        def pageSize=params.int("pageSize",10)
        def filter=[:]
        if(params.from=="user"){
            filter.token=token
        }else{
            def user=userService.info(token)
            def managerVillager=user.managerVillager
            filter.villager=managerVillager
        }
        if(params.status){
            def statusList=params.status.split(",") as ArrayList
            filter.status=['$in':statusList]
        }
        def list=this.findAll(filter,(pageNumber-1)*pageSize,pageSize,[dateCreated:-1])
        list.items.each{ order->
            order.userShowChangeButton=userShowChangeButton[order.status]
            order.managerShowChangeButton=managerShowChangeButton[order.status]
            order.createdDate=DateTools.formatDate(order.dateCreated)
        }
        return list
    }
    def selectById(id){
        def order=this.findById(id)
        order.createdDate=DateTools.formatDate(order.dateCreated)
        order.createdDateDetail=DateTools.formatDate3(order.dateCreated)
        order.payTime=DateTools.formatDate3(order.payTime)
        order.userShowChangeButton=userShowChangeButton[order.status]
        return order
    }
}

/**
 *     "country" : "1000100",
 *     "strCountry" : "宽城县城",
 *     "villager" : "1000101",
 *     "strVillager" : "兆丰小区",
 *     "name" : "王博",
 *     "phone" : "13249594594",
 *     "token" : "oCu4B5MbRdvCxLsGmymxp4FoLlNs",
 *     "sum" : 10.2, 商品金额
 *     realSum:10 实际金额
 *     couponId:优惠券id
 *     couponSum:优惠券减免额
 *     "status" : "DONG",
 *      strStatus:处理中
 *     "code" : "2104122586",
 *     goods:[
 *     {
 *         id,name,sum,oldSum,indexImage,buyCount,nature,strNature,category,detailFileList,remark
 *     }
 *     ]
 *     payTime:支付时间
 *     payExpirationTime:支付过期时间
 *     cancelTime:订单取消时间
 *
 */
