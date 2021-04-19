package wx.goods

import shareshopping.BaseController

class WxOrderController extends BaseController{
    def orderService
    def created() {
        def token=request.getHeader("token")
        def map=request.getJSON() as HashMap
        def result=orderService.create(token,map)
        rv(result.data,result.code,result.message)
    }
    def findById(){
        def id=params.id
        rv(orderService.findById(id))
    }
    def userOrderList(){
        def token=request.getHeader("token")
        rv(orderService.userOrderList(token,params))
    }
}
