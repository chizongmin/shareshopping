package order

import base.InvalidParameterException
import goods.GoodsService
import mongo.MongoService
import shareshopping.Code
import shareshopping.DateTools
import user.UserCouponService

class OrderService  extends MongoService{
    def userAddressService
    GoodsService goodsService
    def orderNumberService
    UserCouponService userCouponService
    def statusNameMap=[
            DONG:"处理中",WAIT_PAY:"待支付","DELIVERY":"配送中",
            WAIT_CONFIRM:"待确认",COMPLETED:"已完成",RETURNED:"已退货",
            RETURN_DOING:"退货处理中",CANCELED:"已取消"
    ]
    def availableChangeMap=[
            DONG:["WAIT_PAY"],
            DELIVERY:["DONG"],
            WAIT_CONFIRM:["DELIVERY"],
            COMPLETED:["WAIT_CONFIRM"],
            RETURN_DOING:["DONG","DELIVERY","WAIT_CONFIRM"],
            RETURNED:["RETURN_DOING"],
            CANCELED:["WAIT_PAY"]
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
        def order=[token:token,status:"WAIT_PAY",strStatus:statusNameMap.WAIT_PAY,code:orderNumberService.created()]
        def userAddress=userAddressService.findById(map.addressId)
        order.putAll(userAddress.subMap(["country","strCountry","villager","strVillager","name","phone","detail"]))
        def sum=0
        def goods=[]
        for(def item:map.goods){
            def goodsDetail=goodsService.findById(item.id)
            if(!goodsDetail){ //不存在，商品已下架
                result.code= Code.goodsDelete
                result.message="${item.name} 已下架"
                //还原库存
                goodsService.recoverGoodsNumber(goods)
                return result
            }
            def reduceNumber=goodsService.reduceNumber(item.id,item.count)
            if(!reduceNumber){ //库存不足
                result.code= Code.goodsEmpty
                result.message="${item.name} 库存不足"
                //还原库存
                goodsService.recoverGoodsNumber(goods)
                return result
            }
            def saveMap=goodsDetail.subMap(["id","name","indexImage","sum","oldSum","nature","strNature","category","detailFileList","remark"])
            saveMap.buyCount=item.count
            goods<<saveMap
            sum+=goodsDetail.sum*item.count
        }
        order.goods=goods
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
            if(coupon.sum>sum){
                result.code= Code.couponGtSum
                result.message="优惠券金额大于商品金额"
                goodsService.recoverGoodsNumber(goods)
                return result
            }
            order.coupon=[id:coupon.id,name:coupon.name,sum:coupon.sum,type:coupon.type]
            order.realSum=sum-coupon.sum
        }
        order=this.save(order)
        result.data=order
        return result
    }
    def paySuccess(orderId){
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
        return order
    }
    def updateStatus(token,map){
        def result=[code:200]
        def orderId=map.orderId
        def toStatus=map.toStatus
        def order=this.updateOne([id:orderId,status:['$in':availableChangeMap[toStatus]]],[status:toStatus,strStatus: statusNameMap[toStatus]])
        if(!order){
            result.code=Code.orderStatusChangeError
            result.message="订单状态错误，请刷新数据后重新操作"
            return result
        }
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
        def filter=[token:token]
        if(params.status){
            filter.status=params.status
        }
        def list=this.findAll(filter,(pageNumber-1)*pageSize,pageSize,[dateCreated:-1])
        return list
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
 *     cancelTime:订单取消时间
 *
 */
