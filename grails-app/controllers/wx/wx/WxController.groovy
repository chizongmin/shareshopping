package wx.wx

import shareshopping.BaseController

class WxController extends BaseController{
    def wxService
    def fetchOpenId () {
        def code=params.code
        def data=wxService.fentchOpenId(code)
        rv(data)
    }
}
