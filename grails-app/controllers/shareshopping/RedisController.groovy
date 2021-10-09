package shareshopping

import base.GedisService
import grails.converters.JSON

class RedisController extends BaseController{
    GedisService gedisService
    def getValue() {
        def key=params.key
        def strValue=gedisService.hmget(key)
        if(strValue){
            strValue= JSON.parse(strValue)
        }
        rv(strValue)
    }
    def setValue(){
        def json=request.getJSON()
        gedisService.memoize("test",json.toString(),60)
        rv("test")
    }
}
