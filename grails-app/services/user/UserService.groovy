package user

import mongo.MongoService
import shareshopping.DateTools

class UserService extends MongoService{
    UserScoreActivityService userScoreActivityService
    UserCouponService userCouponService
    @Override
    String collectionName() {
        "user"
    }
    def list(params){
        def pageNumber=params.int("pageNumber",1)
        def pageSize=params.int("pageSize",10)
        def filter=[:]
        if(params.startTime&&params.endTime){
            filter.'$and'=[[dateCreated:['$gte': DateTools.parseDate(params.startDate)]], [dateCreated:['$lte':DateTools.parseDate(params.endDate)]]]
        }
        if(params.country){
            filter.country=params.country
        }
        if(params.villager){
            filter.villager=params.villager
        }
        return this.findAll(filter,(pageNumber-1)*pageSize,pageSize,[dateCreated:-1])
    }
    def info(token){
        def user=this.findOne([token:token])
        return user
    }
    def home(token){
        def user=this.info(token)
        def couponCount=userCouponService.count([token:token,status:"ENABLE"])
        user.couponCount=couponCount
        return user
    }
    def upsertUser(openid){
        def user=this.findOne([token:openid])
        if(!user){
            user=this.save([token:openid,score:0,info:[:]])
        }
        return user
    }
    def updateInfo(token,infoMap){
        def user=this.findOne([token:token])
        this.updateById(user.id,[info:infoMap])
    }
    def addScore(token,order){
        def realSum=order.realSum
        def scorePercent=order.scorePercent?:1
        def addScore=new BigDecimal(realSum*scorePercent).setScale(0, BigDecimal.ROUND_HALF_UP).intValue()
        def user=this.updateIncOne([token:token],[:],[score:addScore])
        def toSave=[token:token,score:user.score,change:addScore,action:"plus",type:"consume"]
        userScoreActivityService.save(toSave)
    }
}
/**
 *  "_id" : "603a300b0879b9119ca8ba10",
 *     "token" : "oCu4B5MbRdvCxLsGmymxp4FoLlNs",
 *     "score" : NumberInt(0),
 *     "dateCreated" : ISODate("2021-02-27T11:42:03.698+0000"),
 *     "lastUpdated" : ISODate("2021-04-17T14:33:58.648+0000"),
 *     "info" : {*         "country" : "",
 *         "gender" : NumberInt(0),
 *         "province" : "",
 *         "city" : "",
 *         "avatarUrl" : "https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132",
 *         "nickName" : "微信用户",
 *         "language" : ""
 *     }
 */
