package order

import base.InvalidParameterException
import mongo.MongoService
import shareshopping.DateTools

class OrderService  extends MongoService{
    def userAddressService
    def goodsService

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
        def order=userAddress.subMap(["country","strCountry","villager","strVillager","name","phone"])
        def goods=goodsService.findAll([id:['$in':map.goods*.id]])
        def sum=0
        goods.each{item->
            def count=map.goods.find{it.id==item.id}.count
            sum+=item.sum*count
        }
        order.token=token
        order.sum=sum
        order.status="WAIT"
        order.code="test"
        order=this.save(order)
        result.data=order
        return result
    }
    def checkParams(token,map){
        def result=[code:200]
        if(!map.addressId||!map.goods){
            throw InvalidParameterException("addressId and goods can not be null!")
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
}
