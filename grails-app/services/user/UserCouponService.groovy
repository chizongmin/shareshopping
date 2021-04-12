package user

import mongo.MongoService

class UserCouponService extends MongoService{

    @Override
    String collectionName() {
        "userCoupon"
    }
    def selectAll(token){
        def list=this.findAll([token:token],[dateCreated:-1])
        return list
    }
}
