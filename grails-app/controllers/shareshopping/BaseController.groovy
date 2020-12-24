package shareshopping

import base.InvalidParameterException
import grails.converters.JSON

class BaseController {
    //数据返回
    protected rv(data=[:],code=200,message="success") {
        def result=[data:data,code:code,message:message]
        render result as JSON
    }
    def handleInvalidParameterException(InvalidParameterException e) {
        log.error(e.message,e)
        def result=[data:[:],code:406,message:e.message]
        render result as JSON
    }
    def handleException(Exception e) {
        log.error(e.message,e)
        def result=[data:[:],code:500,message:e.message]
        render result as JSON
    }
}
