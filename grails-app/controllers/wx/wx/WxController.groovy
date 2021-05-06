package wx.wx

import shareshopping.BaseController
import wx.WxService

class WxController extends BaseController{
    WxService wxService
    def fetchOpenId () {
        def code=params.code
        def data=wxService.fetchOpenId(code)
        rv(data)
    }
    def fetchQRCode(){
        def scene=params.scene
        def data=wxService.fetchQRCode(scene)
        rv(data)
    }
}
