package wx.user

import shareshopping.BaseController

class WxUserController extends BaseController {
    def userService

    def info() {
        def token = request.getHeader("token")
        def result = userService.info(token)
        rv(result)
    }
    def updateInfo() {
        def token = request.getHeader("token")
        def map=request.getJSON() as HashMap
        userService.updateInfo(token,map)

        rv()
    }
}
