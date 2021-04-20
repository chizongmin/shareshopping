package goods

import base.InvalidParameterException
import mongo.MongoService

class GoodsBagService extends MongoService{

    @Override
    String collectionName() {
        "goodsBag"
    }
    def goodsService
    //total,goodsId,
    def saveTobag(token,map){
        if(!token||!map.goodsId){
            throw new InvalidParameterException("code and id can not be null!")
        }
        def bag=this.findOne([token:token,goodsId:map.goodsId])
        if(bag){
            def total=bag.total+1
            this.updateById(bag.id,[total:total])

        }else{
            def toSave=[token:token,goodsId: map.goodsId,total:1]
            this.save(toSave)
        }
    }
    def list(token){
        def result=[]
        def list=this.findAll([token:token],[dateCreated:-1])
        list.each{item->
            def goods=goodsService.findById(item.goodsId)
            def dataMap=goods.subMap(["id","name","sum","oldSum","remark","indexImage","number","saleNumber"])
            if(!dataMap.remark){
                dataMap.remark=""
            }
            if(!dataMap.saleNumber){
                dataMap.saleNumber=0
            }
            dataMap.bagTotal=item.total
            dataMap.checked=true //默认选中
            result<<dataMap
        }
        return result
    }
    def updateTotal(token,map){
        def total=map.total
        if(!total||total<1){
            total=1
        }
        def bag=this.findOne([token:token,goodsId:map.id])
        this.updateById(bag.id,[total:total])
    }
    def deleteByIds(token,map){
        def ids=map.ids as ArrayList
        this.delete([token:token,goodsId:['$in':ids]])
    }
}
/**
 *     "_id" : "601d54b40879b92d6c0e0fa8",
 *     "token" : "oCu4B5MbRdvCxLsGmymxp4FoLlNs",
 *     "goodsId" : "5ffc583f0879b95388ce384d",
 *     "total" : NumberInt(1) 收藏数量
 *
 */
