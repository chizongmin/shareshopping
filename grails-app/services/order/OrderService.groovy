package order

import base.InvalidParameterException
import goods.GoodsService
import mongo.MongoService
import shareshopping.Code
import shareshopping.DateTools

class OrderService  extends MongoService{
    def userAddressService
    GoodsService goodsService
    def orderNumberService
    def statusNameMap=[
            DONG:"处理中",WAIT_PAY:"待支付","DELIVERY":"配送中",
            WAIT_CONFIRM:"待确认",COMPLETED:"已完成",RETURNED:"已退货",
            RETURN_DOING:"退货处理中"
    ]
    @Override
    String collectionName() {
        "order"
    }
    def create(token,map){
        def result=this.checkParams(token,map)
        if(result.code!=200){
            return result
        }
//        addressId:addressId,couponId:coupon.id,goods:confirmToService,remark:remark
        def userAddress=userAddressService.findById(map.addressId)
        def order=userAddress.subMap(["country","strCountry","villager","strVillager","name","phone","detail"])
        def sum=0
        def goods=[]
        for(def item:map.goods){
            def goodsDetail=goodsService.findById(item.id)
            if(!goodsDetail){ //不存在，商品已下架
                result.code= Code.goodsDelete
                result.message="${item.name} 已下架"
                //还原库存
                goodsService.addGoodsNumber(goods)
                return result
            }
            def reduceNumber=goodsService.reduceNumber(item.id,item.count)
            if(!reduceNumber){ //库存不足
                result.code= Code.goodsEmpty
                result.message="${item.name} 库存不足"
                //还原库存
                goodsService.addGoodsNumber(goods)
                return result
            }
            def saveMap=goodsDetail.subMap(["id","name","indexImage","sum"])
            saveMap.buyCount=item.count
            goods<<saveMap
            sum+=goodsDetail.sum*item.count
        }
        order.token=token
        order.sum=sum
        order.status="WAIT"
        order.statusName=statusNameMap.WAIT
        order.code=orderNumberService.created()
        order=this.save(order)
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
