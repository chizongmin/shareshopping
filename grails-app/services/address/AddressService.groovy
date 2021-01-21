package address

import base.InvalidParameterException
import mongo.MongoService

class AddressService extends MongoService{

    @Override
    String collectionName() {
        "address"
    }
    def list(){
        def list=this.findAll([:],[sort:1])
        def listMap=list.groupBy {it.fid}
        def result=list.findAll{it.fid=="0"}
        def children=list.findAll{it.fid!="0"}
        children.each{child->
            def parent=list.find{it.id==child.fid}
            child.strFid=parent?.name
        }
        result.each{item->
            item.children=listMap[item.id]
        }
        return result
    }
    def upsertAddress(params){
        def toUpdate=params.subMap(["id","name","fid","status","desc"])
        if(params.id&&params.action=="update"){
            this.updateById(params.id,toUpdate)
        }else{
            def exists=this.findById(params.id)
            if(exists){
                throw new InvalidParameterException("ID 已存在！")
            }
            toUpdate.sort=10000
            this.save(toUpdate)
        }
    }
    def updateSort(map){
        def list=map.list
        list.eachWithIndex{ entry,  i ->
            this.updateById(entry.id,[sort:i])
        }
    }
}
