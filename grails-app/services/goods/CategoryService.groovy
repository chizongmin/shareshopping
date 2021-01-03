package goods

import base.InvalidParameterException
import mongo.MongoService
import shareshopping.NameMap

class CategoryService extends MongoService{

    @Override
    String collectionName() {
        "category"
    }
    def editList(){
        def list=this.findAll([:],[sort:1])
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
}
