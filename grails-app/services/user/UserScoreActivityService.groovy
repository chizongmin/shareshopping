package user

import mongo.MongoService
import shareshopping.DateTools

class UserScoreActivityService extends MongoService{
    UserService userService
    @Override
    String collectionName() {
        "userScoreActivity"
    }
    def scoreActivity(token,params){
        def pageNumber=params.int("pageNumber",1)
        def pageSize=params.int("pageSize",10)
        def data=this.findAll([token:token],(pageNumber-1)*pageSize,pageSize,[dateCreated:-1])
        data.items.each{
            if(it.type=="consume"){
                it.strType="消费积分"
            }else if(it.type=="exchangeCoupon"){
                it.strType="兑换优惠券"
            }
            it.dateCreated= DateTools.formatDate2(it.dateCreated)
        }
        def user=userService.info(token)
        data.score=user.score
        return data
    }
}
