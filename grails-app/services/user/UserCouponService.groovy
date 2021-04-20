package user

import mongo.MongoService

class UserCouponService extends MongoService{
    def statusNameMap=[
            "ENABLE":"可用",
            "USED":"已使用"
    ]
    @Override
    String collectionName() {
        "userCoupon"
    }
    def selectAll(token){
        def list=this.findAll([token:token],[dateCreated:-1])
        return list
    }
    def addCoupon(token,coupon){
        def toSave=coupon.subMap(["name","sum","type"])
        toSave.token=token
        return this.save(toSave)
    }
    def useCoupon(token,couponId){
        def data=this.updateOne([token:token,id:couponId,status:"ENABLE"],[status:"USED"])
        return data
    }
    def recoverCoupon(token,couponId){
        def data=this.updateOne([token:token,id:couponId],[status:"ENABLE"])
        return data
    }
}
/**
 * token,name,sum,status,strStatus
 */
