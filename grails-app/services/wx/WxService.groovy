package wx

import base.HttpService
import base.InvalidParameterException
import mongo.ConfigService
import org.springframework.beans.factory.InitializingBean
import sun.misc.BASE64Encoder

import java.util.concurrent.ConcurrentHashMap

class WxService implements InitializingBean{
    static CACHE=new ConcurrentHashMap()
    ConfigService configService
    static weixin_url="https://api.weixin.qq.com"
    def userService
    def fetchQRCode(scene){
        def accessToken=this.getAccessToken()
        def postParams=[scene:scene,width:280]
        def bytes=HttpService.QRCode(weixin_url,"wxa/getwxacodeunlimit?access_token="+accessToken,postParams)
        BASE64Encoder encoder = new BASE64Encoder()
        String png_base64 = encoder.encodeBuffer(bytes)//转换成base64串
        png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
        return "data:image/png;base64,"+png_base64
    }
    def fetchOpenId(code) {
        if(!code){
            throw new InvalidParameterException("code can not be null!")
        }
        def params=[appid:CACHE.appid,secret:CACHE.secret,js_code:code,grant_type:"authorization_code"]
        def res=HttpService.get(weixin_url,"sns/jscode2session",params)
        def openid=res.openid
        userService.upsertUser(openid)
        return [openid:openid]
    }
    def fetchAccessToken(){
        def params=[appid:CACHE.appid,secret:CACHE.secret,grant_type:"client_credential"]
        def res=HttpService.get(weixin_url,"cgi-bin/token",params)
        def access_token=res.access_token
        return access_token
    }
    def getAccessToken(){
        def config=configService.findById("accessToken")
        if(!config.accessToken){
            def access_token=this.fetchAccessToken()
            configService.updateById("accessToken",[accessToken:access_token,times:new Date().time])
        }
        return config.accessToken
    }
    @Override
    void afterPropertiesSet() throws Exception {
        def config=configService.findById("miniProgram")
        CACHE.appid=config.appid
        CACHE.secret=config.secret
        //小于半小时，更新 1000*1800
        def updateAccessToken=configService.updateOne([id:"accessToken",times:['$lt':new Date().time-(1000*1800)]],[times:new Date().time])
        if(updateAccessToken){ //更新成功 赋值accessToken
            def access_token=this.fetchAccessToken()
            configService.updateById("accessToken",[accessToken:access_token])
        }
    }
}
