package goods

import mongo.MongoService
import shareshopping.NameMap

class GoodsService extends MongoService{
    def categoryService
    @Override
    String collectionName() {
        "goods"
    }
    def upsertGoods(goods){
        if(goods.id){
            this.updateById(goods.id,goods)
            //更新所有列表商品
            def allCategory=categoryService.findAll([:])
            allCategory.each{category->
                def goodsList=category.goods?:[]
                def index=goodsList.findIndexOf {it.id==goods.id}
                if(index!=-1){
                    goodsList[index]=goods
                }
                categoryService.updateById(category.id,[goods:goodsList])
            }
        }else{
            this.save(goods)
        }
    }
    def editList(category,search){
        def filter=[category:category]
        if(search){
            filter.name=['$regex':search, '$options': "i"]
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
            def url=it.url.replaceFirst("/api","")
            def map=[id:it.id,url:url]
            detailPic<<map
        }
        goods.detailFileList=detailPic
        return goods
    }
}
