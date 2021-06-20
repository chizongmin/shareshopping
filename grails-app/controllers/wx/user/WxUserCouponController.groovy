package wx.user

import goods.CouponService
import shareshopping.BaseController
import user.UserCouponService

class WxUserCouponController extends BaseController{
    UserCouponService userCouponService
    CouponService couponService
    def userCoupon() {
        def token = request.getHeader("token")
        def status=params.status
        rv(userCouponService.selectAll(token,status))
    }
    def systemCouponList(){
        rv(couponService.availableList())
    }
    def exchangeCoupon(){
        def token = request.getHeader("token")
        def map=request.getJSON() as HashMap
        def result=couponService.exchangeCoupon(token,map.couponId)
        rv(result.data,result.code,result.message)
    }
}
