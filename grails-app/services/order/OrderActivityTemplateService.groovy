package order

import mongo.MongoService

class OrderActivityTemplateService extends MongoService{

    @Override
    String collectionName() {
        "orderActivityTemplate"
    }
    def statusContentMap(){
        def list=this.findAll([:])
        return list.collectEntries{[it.status,it.content]}
    }
}
