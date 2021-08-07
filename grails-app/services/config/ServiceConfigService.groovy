package config

import mongo.MongoService

class ServiceConfigService extends MongoService{

    @Override
    String collectionName() {
        "config"
    }
    def index(){
        def config=this.findAll([:])
        def deleteFields=["miniProgram","accessToken"]
        def outPut=config.findAll{!(it.id in deleteFields)}
        return outPut.collectEntries{[it.id,it.value]}
    }
    def update(map){
        this.updateById(map.key,[value:map.value])
    }
}
