package background.manager

import shareshopping.BaseController
import user.ManagerService

class ManagerController extends BaseController{
    ManagerService managerService
    def info() {
        def uid=params.uid
//        def manager=managerService.findById(uid)
        def manager=managerService.findOne([id:"5fe1aaab2e180d73c486f4cf01"])
        rv(manager)
    }
    def logout(){
        def token =request.getHeader("X-TOKEN")
        managerService.logout(token)
        rv()
    }
    def changePassword(){
        def uid=params.uid
        def result=managerService.changePassword(uid,request.getJSON())
        rv(result.data,result.code,result.message)
    }
}
