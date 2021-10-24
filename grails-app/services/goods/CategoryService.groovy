package goods

import base.GedisService
import base.InvalidParameterException
import grails.converters.JSON
import mongo.MongoService
import org.springframework.beans.factory.annotation.Value
import shareshopping.NameMap

class CategoryService extends MongoService{
    @Override
    String collectionName() {
        "category"
    }
    @Value('${station}')
    String station
    GedisService gedisService
    GoodsService goodsService
    def editList(params){
        def filter=[:]
        if(params.status){
            filter.status=params.status
        }
        def list=this.findAll(filter,[sort:1])
        list.each{ category->
            category.strStatus= NameMap.statusMap[category.status]
        }
        return list
    }
    def updateInfo(params){
        if(!params.id){
            throw new InvalidParameterException("id can not be null!")
        }
        def category=this.updateById(params.id,params)
        return category
    }
    def updateSort(params){
        def sortList=params.list
        sortList.eachWithIndex { entry, i ->
            this.updateById(entry.id,[sort: i])
        }
    }
    def addGoods(params){
        def id=params.id
        def addGoods=params.goods
        if(!id||!addGoods){
            return
        }
        def category=this.findById(id)
        def goods=category.goods?:[]
        addGoods=addGoods.findAll{!(it.id in goods*.id)}
        goods.addAll(addGoods)
        this.updateById(id,[goods:goods])
    }
    def deleteGoods(params){
        def id=params.id
        def goodsId=params.goodsId
        if(!id||!goodsId){
            return
        }
        def category=this.findById(id)
        def goods=category.goods?:[]
        def saveGoods=goods.findAll{it.id!=goodsId}
        this.updateById(id,[goods:saveGoods])
    }
    def updateGoodsSort(params){
        def goods=params.goods
        def id=params.id
        this.updateById(id,[goods:goods])
    }
    def tabList(){
        def key=station+"tabList"
        def result=gedisService.get(key)?:[]
        if(!result){
            result=this.setTabListCache()
        }else{
            result= JSON.parse(result)
        }
        return result
    }
    def tabMapGoods(){
        def key=station+"tabMapGoods"
        def result=gedisService.get(key)?:[:]
        if(!result){
            result=this.setTabMapGoodsCache()
        }else{
            result= JSON.parse(result)
        }
        return result
    }
    def setTabListCache(){
        def result=[]
        def key=station+"tabMapGoods"
        def list=this.findAll([status:"ENABLE"],[sort:1])
        list.eachWithIndex{ item, i ->
            def map=[id:item.id,name:item.name]
            if(i==0){
                def goodsList=goodsService.findAll([id:['$in':item.goods*.id]])
                def goods=goodsList?.collect{[id:it.id,name:it.name,sum:it.sum,oldSum:it.oldSum,
                                              remark:it.remark?:"",indexImage:it.indexImage,number:it.number,saleNumber:it.saleNumber?:0
                ]}
                map.goods=goods
            }else{
                map.goods=[]
            }
            result<<map
        }
        gedisService.memoize(key,(result as JSON).toString(),600)
        return result
    }
    def setTabMapGoodsCache(){
        def key=station+"tabMapGoods"
        def result=[:]
        def list=this.findAll([status:"ENABLE"])
        list.each{item->
            def goodsList=goodsService.findAll([id:['$in':item.goods*.id]])
            def goods=goodsList?.collect{[id:it.id,name:it.name,sum:it.sum,oldSum:it.oldSum,
                                          remark:it.remark?:"",indexImage:it.indexImage,number:it.number,saleNumber:it.saleNumber?:0
            ]}
            result[item.id]=goods
        }
        gedisService.memoize(key,(result as JSON).toString(),600)
        return result
    }
}
/**
 *      "_id" : "rexiao",
 *     "name" : "特价商品",
 *     "status" : "ENABLE",
 *     "sort" : NumberInt(0),
 *     "desc" : "此为推荐商品",
 *     "strStatus" : "启用",
 *     "goods" : [
 *          {goods}
 * ]
 */
