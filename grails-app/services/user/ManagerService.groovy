package user

import base.Code
import base.InvalidParameterException
import mongo.MongoService

class ManagerService  extends MongoService{

    @Override
    String collectionName() {
        "manager"
    }
    def login(map){
        def result=[code:200]
        def account=map.account
        def password=map.password
        if(!account||!password){
            throw new InvalidParameterException("account and password can not be null!")
        }
        password=password.encodeAsSHA256()
        def manager=this.findOne([account:account,password:password])
        if(!manager){
            return Code.fillCode(Code.accountPasswordError)
        }else{
            def data=manager.subMap(["name","account","roles"])
            data.token="pendding"
            result.data=data
            return result
        }
    }
    def logout(uid){
        //clean token
    }
    def changePassword(uid,map){
        def result=[code:200]
        def oldPassword=map.oldPassword
        def manager=this.findOne([id:uid,password:oldPassword.encodeAsSHA256()])
        if(!manager){
            return Code.fillCode(Code.oldPasswordError)
        }
        def newPassword=map.newPassword..encodeAsSHA256()
        this.updateById(manager.id,[password:newPassword])
        result
    }
}
