package background.user

import shareshopping.BaseController

class UserController extends BaseController{
    def userService
    def list() {
        def data=userService.list(params)
        rv(data)
    }
}
