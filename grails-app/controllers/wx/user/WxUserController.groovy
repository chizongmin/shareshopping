package wx.user

import shareshopping.BaseController
import user.UserScoreActivityService

class WxUserController extends BaseController {
    def userService
    UserScoreActivityService userScoreActivityService

    def home() {
        def token = request.getHeader("token")
        def result = userService.home(token)
        rv(result)
    }
    def updateInfo() {
        def token = request.getHeader("token")
        def map=request.getJSON() as HashMap
        userService.updateInfo(token,map)
        rv()
    }
    def scoreActivity(){
        def token = request.getHeader("token")
        rv(userScoreActivityService.scoreActivity(token,params))
    }
}
