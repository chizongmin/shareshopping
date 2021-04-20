package user

import mongo.MongoService

/**
 *     "addressId" : "1000101",
 *     "strCountry" : "宽城县城",
 *     "strVillager" : "兆丰小区",
 *     "name" : "王博",
 *     "phone" : "13249594594",
 *     "detail" : "一号楼一单元101",
 *     "default" : true,
 *     "country" : "1000100",
 *     "villager" : "1000101",
 *     "token" : "oCu4B5MbRdvCxLsGmymxp4FoLlNs"
 */
class UserAddressService extends MongoService{

    @Override
    String collectionName() {
        "userAddress"
    }
    def addressService
    def list(token){
        def all=this.findAll([token:token],[lastUpdated:-1])
        return all
    }
    def upsertAddress(token,map){
        def result
        def addressId=map.addressId
        def address=addressService.findById(addressId)
        def fAddress=addressService.findById(address.fid)
        def dataMap=[addressId:addressId,strCountry:fAddress?.name,strVillager:address?.name,name:map.name,phone:map.phone,
                detail:map.detail,default:map.default,country:map.country,villager:map.villager
        ]
        if(map.default){//更新所有其他的都为非默认
            this.updateBatch([token:token],[default:false])
        }
        if(map.id){
            result=this.updateById(map.id,dataMap)
        }else{
            dataMap.token=token
            result=this.save(dataMap)
        }
        return result
    }
    def defaultAddress(token){
        def address=this.findOne([token:token,default:true])
        if(!address){//最近被更新的
            def all=this.findAll([token:token])?:[]
            address=all.max{it.lastUpdated}
        }
        return address?:[:]
    }
}
