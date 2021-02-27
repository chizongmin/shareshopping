package user

import mongo.MongoService
import shareshopping.DateTools

class UserService extends MongoService{

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
}
