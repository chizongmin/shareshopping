package goods

import mongo.MongoService

/**
 * 优惠券列表
 */
class CouponService extends MongoService{

    @Override
    String collectionName() {
        "coupon"
    }
    def availableList(){
        def all=this.findAll([status:"ENABLE"],[inx:1])
        return all
    }
}
