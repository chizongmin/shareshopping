package shareshopping

import org.springframework.http.HttpStatus


class ParamsInterceptor {
    ParamsInterceptor(){
        match(namespace:"background").excludes(controller:"login1s")
    }
    boolean before() {
        def token = request.getHeader("X-TOKEN")
        if(!token){
            response.sendError(HttpStatus.UNAUTHORIZED.value())
            return false
        }
        def redis=[uid:"5fe1aaab2e180d73c486f4cf01",uName:"admin",token:token]
        params<<redis
        //添加用户操作记录
        def ip = request.getHeader('X-Forwarded-For') ?: request.getRemoteAddr()
        def data = [uid: params.uid, uName: params.uName, controller: params.controller, action: params.action, ip: ip,  params: params]
        log.info("操作记录：${data}")
     /*   def controllerClass = grailsApplication
                .getArtefactByLogicalPropertyName("Controller", controllerName).clazz
        def annotation = controllerClass.getMethod(actionName).getAnnotation(LogFlag.class)
        if (annotation?.value() == "SU") {
            kafkaSendService.sendLog(params.domain, data)
        }*/
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
