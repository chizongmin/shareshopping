package background.manager

import shareshopping.BaseController
import user.ManagerService

class ManagerController extends BaseController{
    ManagerService managerService
    def info() {
        def uid=params.uid
        def manager=managerService.findById(uid)
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
