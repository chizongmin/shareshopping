package goods

import mongo.MongoService
import shareshopping.Code

class GoodsTagService extends MongoService{

    @Override
    String collectionName() {
        "goodsTag"
    }
    def addTag(tag){
        def result=[code:200]
        if(!tag){
            return result
        }
        def one=this.findById(tag)
        if(one){
            result.code= Code.tagRepeat
            result.message="标签${tag}已存在"
        }else{
            this.save([id:tag])
        }
        return result
    }
    def nameLike(keyword){
        def filter=[:]
        if(keyword){
            filter=[id:['$regex':keyword, '$options': "i"]]
        }
        def tags=this.findAll(filter)
        return tags*.id
    }
}
