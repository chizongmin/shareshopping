package goods

import base.GedisService
import base.InvalidParameterException
import grails.converters.JSON
import mongo.MongoService
import shareshopping.NameMap
import user.UserCouponService

class GoodsService extends MongoService{
    def categoryService
    UserCouponService userCouponService
    GedisService gedisService
    @Override
    String collectionName() {
        "goods"
    }
    def upsertGoods(goods){
        this.checkGoodsParams(goods)
        if(goods.id){
            goods.remove("number") //去掉库存
            this.updateById(goods.id,goods)
            //更新redis 缓存
            this.resetReidsCache([goods.id])
        }else{
            this.save(goods)
        }
    }
    def checkGoodsParams(goods){
        if(!(goods.number instanceof Number)||goods.number<0){
            throw new InvalidParameterException("goods number not correct!")
        }
    }
    def editList(category,search){
        def filter=[category:category]
        if(search){
            filter.'$or'=[[name:['$regex':search, '$options': "i"]],[tags:['$regex':search, '$options': "i"]]]
        }
        def list=this.findAll(filter,[dateCreated:-1])
        list.each{ goods->
            goods.strStatus= NameMap.statusMap[goods.status]
            goods.strNature= NameMap.natureMap[goods.nature]
            if(!goods.detailFileList){
                goods.detailFileList=[]
            }
        }
        return list
    }
    def deleteGoods(id){
        this.delete([id:id])
        //更新所有列表商品
        def allCategory=categoryService.findAll([:])
        allCategory.each{category->
            def goodsList=category.goods?:[]
            def updateGoods=goodsList.findAll{it.id!=id}
            categoryService.updateById(category.id,[goods:updateGoods])
        }
    }
    def addToCategoryList(index){
        def list=this.findAll([status:"ENABLE"],[dateCreated:-1])
        def category=categoryService.findById(index)
        def goodsIds=category.goods*.id?:[]
        list=list.findAll{!(it.id in goodsIds)}?:[]
        list.each{ goods->
            goods.strStatus= NameMap.statusMap[goods.status]
            goods.strNature= NameMap.natureMap[goods.nature]
        }
        def listMap=list.groupBy {it.category}
        return listMap
    }
    def selectById(id){
        def goods=this.findById(id)
        def detailFileList=goods.detailFileList?:[]
        def detailPic=[]
        detailFileList.each{
//            def url=it.url.replaceFirst("/api","")
            def map=[id:it.id,url:it.url]
            detailPic<<map
        }
        goods.detailFileList=detailPic
        goods.saleNumber=goods.saleNumber?:0
        return goods
    }
    def selectByIdWithCache(id){
        def goods=gedisService.get(id)
        if(!goods){
            this.setGoodsDetailCache([id])
        }else{
            goods=JSON.parse(goods)
        }
        return goods
    }
    def selectByIds(ids){
        def goods=this.findAll([id:['$in':ids]])
        def result=goods.collect{[id:it.id,name:it.name,sum:it.sum,oldSum:it.oldSum,
                                 remark:it.remark?:"",indexImage:it.indexImage,number:it.number,saleNumber:it.saleNumber?:0
        ]}
        return result
    }
    def reduceNumber(id,number){
        def data=this.updateIncOne([id:id,number:['$gte':number]],[:],[number:-number,saleNumber:number])
        return data
    }
    def recoverGoodsNumber(goods){
        goods.each{item->
            this.addNumber(item.id,item.buyCount)
        }
    }
    def addNumber(id,number){
        def data=this.updateIncOne([id:id],[:],[number:number,saleNumber:number])
        return data
    }
    def confirmGoods(token,ids){
        def goods=this.selectByIds(ids)
        def userCoupons=userCouponService.selectAll(token,"ENABLE")
        return [goods:goods,userCoupons:userCoupons]
    }
    //缓存处理
    def setGoodsDetailCache(ids){
        ids.each{id->
            def goods=this.selectById(id)
            if(goods){
                gedisService.memoize(id,goods.toJson().toString(),600)
            }
        }
    }
    //更改商品更新缓存
    def resetReidsCache(ids){
        this.setGoodsDetailCache(ids)
        categoryService.setTabListCache()
        categoryService.setTabMapGoodsCache()
    }
}
/**
 {
 "_id" : "6006e0790879b90d9c4c939c",
 "number" : NumberInt(10),
 "lastUpdated" : ISODate("2021-02-07T13:03:41.533+0000"),
 "dateCreated" : "2021-01-19T13:36:57Z",
 "nature" : "normal",
 "indexImage" : "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQA,
 "name" : "特价水果这hi是一个产巢字段测试个产巢字段测试（5斤）",
 "saleNumber" : NumberInt(1222),
 "oldSum" : 22.9,
 "sum" : 10.2,
 "category" : "vegetables",
 "status" : "ENABLE",
 "desc" : "工作中我们有的时候会需要去获取一张图片的颜色值或者颜色代码，
 "id" : "6006e0790879b90d9c4c939c",
 "strNature" : "一般商品",
 "strStatus" : "启用",
 "detailFileList" : [
 {
 "uid" : NumberLong(1611239977617),
 "name" : "file",
 "id" : "600992290879b91f183b4b32",
 "url" : "/api/file/preview?id=600992290879b91f183b4b32",
 "status" : "success"
 }
 ],
 "remark" : "超市价￥25" //标签
 commission:配送佣金
 }
 */
