package address

import base.InvalidParameterException
import mongo.MongoService

class AddressService extends MongoService{

    @Override
    String collectionName() {
        "address"
    }
    def list(keyword){
        def list=assembleList(keyword)
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
    def assembleList(keyword){
        def list=[]
        if(keyword){
            list=this.findAll([name:['$regex':keyword, '$options': "i"],fid:['$ne':"0"]],[sort:1])?:[]
            def groupBy=children.groupBy {it.fid}
            groupBy?.each{k,v->
                list<<this.findById(k)
            }

        }else{
            list=this.findAll([:],[sort:1])
        }
        return list
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
    def filterList(){
        def list=this.findAll([:],[sort:1]).collect{[value:it.id,label:it.name,fid:it.fid]}
        def country=list.findAll{it.fid=="0"}
        def villager=list.findAll{it.fid!="0"}
        return [country:country,villager:villager]
    }
}
