package wx.user

import shareshopping.BaseController

class WxUserAddressController  extends BaseController{
    def userAddressService
    def addressService
    def upsertAddress(){
        def token=request.getHeader("token")
        def map=request.getJSON() as HashMap
        def result=userAddressService.upsertAddress(token,map)
        rv(result)
    }
    def addressList(){
        def token=request.getHeader("token")
        def result=userAddressService.list(token)
        rv(result)
    }
    def defaultAddress(){
        def token=request.getHeader("token")
        def result=userAddressService.defaultAddress(token)
        rv(result)
    }
    def publicAddress(){
        rv(addressService.list(params.keyword))
    }
}
