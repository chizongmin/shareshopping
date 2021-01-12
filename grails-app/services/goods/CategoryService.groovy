package goods

import base.InvalidParameterException
import mongo.MongoService
import shareshopping.NameMap

class CategoryService extends MongoService{

    @Override
    String collectionName() {
        "category"
    }
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
}
