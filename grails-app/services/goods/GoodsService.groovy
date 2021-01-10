package goods

import mongo.MongoService
import shareshopping.NameMap

class GoodsService extends MongoService{

    @Override
    String collectionName() {
        "goods"
    }
    def upsertGoods(uid,goods){
        if(goods.id){
            this.updateById(goods.id,goods)
        }else{
            this.save(goods)
        }
    }
    def editList(category,search){
        def filter=[category:category]
        if(search){
            filter.name=['$regex':search, '$options': "i"]
        }
        def list=this.findAll(filter,[dateCreated:1])
        list.each{ goods->
            goods.strStatus= NameMap.statusMap[goods.status]
            goods.strNature= NameMap.natureMap[goods.nature]
        }
        return list
    }
    def deleteGoods(id){
        this.delete([id:id])
    }
}
