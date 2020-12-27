package order

import mongo.MongoService

class OrderService  extends MongoService{

    @Override
    String collectionName() {
        "order"
    }
    def list(){

    }
}
