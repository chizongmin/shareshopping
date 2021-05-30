package goods

import mongo.MongoService
import shareshopping.Code
import user.UserCouponService
import user.UserScoreActivityService
import user.UserService

/**
 * 优惠券列表
 */
class CouponService extends MongoService{
    UserService userService
    UserCouponService userCouponService
    UserScoreActivityService userScoreActivityService
    @Override
    String collectionName() {
        "coupon"
    }
    def availableList(){
        def all=this.findAll([status:"ENABLE"],[inx:1])
        return all
    }
    def exchangeCoupon(token,couponId){
        def result=[code:200,message:"兑换成功"]
        def coupon=this.findById(couponId)
        def user=userService.updateIncOne([token:token,score:['$gte':coupon.score]],[:],[score:-coupon.score])
        if(!user){
            result.code= Code.scoreNotEnough
            result.message="积分不足"
            return result
        }
        userScoreActivityService.save([token:token,score:user.score,change:coupon.score,action:"reduce",type:"exchangeCoupon"])
        //向用户添加优惠券
        result.data=userCouponService.addCoupon(token,coupon)
        return result
    }
}
/**
 * id,name,sum,score,type
 */
