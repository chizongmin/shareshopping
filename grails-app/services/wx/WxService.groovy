package wx

import base.HttpService
import base.InvalidParameterException

class WxService {
    static weixin_url="https://api.weixin.qq.com/"
    static String appid="wxe9de3f1de3875026"
    static String secret="2ebc5c282d44e6ed7ba46e510ed9fa1d"
    def userService
    def fentchOpenId(code) {
        if(!code){
            throw new InvalidParameterException("code can not be null!")
        }
        def params=[appid:appid,secret:secret,js_code:code,grant_type:"authorization_code"]
        def res=HttpService.get(weixin_url,"sns/jscode2session",params)
        def openid=res.openid
        userService.upsertUser(openid)
        return [openid:openid]
    }
}
