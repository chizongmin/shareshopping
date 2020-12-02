package shareshopping

import grails.converters.JSON
import grails.util.Environment

class TestController {

    def index() {
        def environment = Environment.current.name
        def data = [environment: environment]
        render data as JSON
    }
}
