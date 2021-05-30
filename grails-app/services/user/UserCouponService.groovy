package user

import mongo.MongoService

class UserCouponService extends MongoService{
    def statusNameMap=[
            "ENABLE":"待使用",
            "USED":"已使用",
            "OUT_DATE":"已过期"
    ]
    @Override
    String collectionName() {
        "userCoupon"
    }
    def selectAll(token){
        def list=this.findAll([token:token,status:"ENABLE"],[dateCreated:-1])
        return list
    }
    def addCoupon(token,coupon){
        def toSave=coupon.subMap(["name","sum","type","expiryDate"])
        toSave.token=token
        toSave.status="ENABLE"
        toSave.strStatus=statusNameMap[toSave.status]
        return this.save(toSave)
    }
    def useCoupon(token,couponId){
        def data=this.updateOne([token:token,id:couponId,status:"ENABLE"],[status:"USED",strStatus:statusNameMap.USED])
        return data
    }
    def recoverCoupon(token,couponId){
        def data=this.updateOne([token:token,id:couponId],[status:"ENABLE",strStatus:statusNameMap.ENABLE])
        return data
    }
}
/**
 * token,name,sum,status,strStatus
 */
