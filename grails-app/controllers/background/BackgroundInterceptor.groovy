package background

import org.springframework.http.HttpStatus
import user.ManagerService
class BackgroundInterceptor {

/*    BackgroundInterceptor(){
        matchAll().excludes(controller:"login")
    }*/
    boolean before() {
        def token = request.getHeader("X-TOKEN")
        if(!token){
            response.sendError(HttpStatus.UNAUTHORIZED.value())
            return false
        }
        def user= ManagerService.cacheMap[token]
        if(!user){
            response.sendError(HttpStatus.FORBIDDEN.value())
            return false
        }
        params<<user?:[:]
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
        return true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
