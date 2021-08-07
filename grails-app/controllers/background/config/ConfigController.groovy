package background.config

import config.ServiceConfigService
import shareshopping.BaseController

class ConfigController extends BaseController{
    ServiceConfigService serviceConfigService
    def index() {
        rv(serviceConfigService.index())
    }
    def update(){
        serviceConfigService.update(request.getJSON())
        rv()
    }
}
