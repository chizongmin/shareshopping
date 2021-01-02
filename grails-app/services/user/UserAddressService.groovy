package user

import mongo.MongoService

class UserAddressService extends MongoService{

    @Override
    String collectionName() {
        "userAddress"
    }
    def filterList(){
        def list=this.findAll([:],[sort:1]).collect{[value:it.id,label:it.name,fid:it.fid]}
        def country=list.findAll{it.fid=="0"}
        def villager=list.findAll{it.fid!="0"}
        return [country:country,villager:villager]
    }
}
