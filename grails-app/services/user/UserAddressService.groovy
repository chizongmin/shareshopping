package user

import mongo.MongoService

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
                detail:map.detail,default:map.default
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
