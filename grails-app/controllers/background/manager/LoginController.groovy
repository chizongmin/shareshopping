package background.manager
import shareshopping.BaseController
import user.ManagerService

class LoginController extends BaseController{
    ManagerService managerService
    def login() {
        def params=request.getJSON()
        def result=managerService.login(params)
        rv(result.data,result.code,result.message)
    }
}
