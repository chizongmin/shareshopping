package shareshopping

import grails.converters.JSON
import grails.util.Environment
import user.UserService

class TestController {
    UserService userService
    def index() {
        def environment = Environment.current.name
        def data = [environment: environment]
        data.user=userService.findAll([:])
        render data as JSON
    }
}
