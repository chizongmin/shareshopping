package user

import mongo.MongoService

class UserScoreActivityService extends MongoService{

    @Override
    String collectionName() {
        "userScoreActivity"
    }
    def scoreActivity(token){
        def list=this.findAll([token:token])
        return list
    }
}
