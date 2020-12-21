package shareshopping

import org.springframework.http.HttpStatus


class ParamsInterceptor {
    ParamsInterceptor(){
        match(namespace:"background").excludes(controller:"login")
    }
    boolean before() {
        def userId = request.getHeader("X-TOKEN")
        if(!userId){
            response.sendError(HttpStatus.UNAUTHORIZED.value())
            return false
        }
        def redis=[uid:"111",uName:"admin"]
        params<<redis
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
