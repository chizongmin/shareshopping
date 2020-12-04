package user

import mongo.MongoService

class UserService extends MongoService{

    @Override
    String collectionName() {
        "user"
    }
}
