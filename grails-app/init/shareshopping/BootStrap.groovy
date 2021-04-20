package shareshopping

import grails.converters.JSON

import java.text.SimpleDateFormat

class BootStrap {

    def init = { servletContext ->
        registerJsonDateMarshaller()
    }
    def destroy = {
    }
    private registerJsonDateMarshaller() {
        JSON.registerObjectMarshaller(Date) { Date date ->
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return sdf.format(date)
        }
    }
}
