package goods

import mongo.MongoService
import shareshopping.Code
import user.UserCouponService
import user.UserService

/**
 * 优惠券列表
 */
class CouponService extends MongoService{
    UserService userService
    UserCouponService userCouponService
    @Override
    String collectionName() {
        "coupon"
    }
    def availableList(){
        def all=this.findAll([status:"ENABLE"],[inx:1])
        return all
    }
    def exchangeCoupon(token,couponId){
        def result=[code:200]
        def coupon=this.findById(couponId)
        def data=userService.updateIncOne([token:token,score:['$gte':coupon.score]],[:],[score:-coupon.score])
        if(!data){
            result.code= Code.scoreNotEnough
            result.message="积分不足"
            return result
        }
        //向用户添加优惠券
        result.data=userCouponService.addCoupon(token,coupon)
        return result
    }
}
/**
 * id,name,sum,score,type
 */
