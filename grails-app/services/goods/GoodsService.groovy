package goods

import mongo.MongoService

class GoodsService extends MongoService{

    @Override
    String collectionName() {
        "goods"
    }
    def editList(){
        def list=this.findAll([status:"ENABLE"],[dateCreated:1])
        return list
    }
}
