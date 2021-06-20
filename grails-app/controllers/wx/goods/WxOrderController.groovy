package wx.goods

import order.OrderActivityService
import order.OrderService
import shareshopping.BaseController

class WxOrderController extends BaseController{
    OrderService orderService
    OrderActivityService orderActivityService
    def create() {
        def token=request.getHeader("token")
        def map=request.getJSON() as HashMap
        def result=orderService.create(token,map)
        rv(result.data,result.code,result.message)
    }
    def findById(){
        def id=params.id
        rv(orderService.selectById(id))
    }
    def userOrderList(){
        def token=request.getHeader("token")
        rv(orderService.userOrderList(token,params))
    }
    def updateStatus(){
        def token=request.getHeader("token")
        def map=request.getJSON() as HashMap
        def result=orderService.updateStatus(token,map)
        rv(result.data,result.code,result.message)
    }
    def orderActivity(){
        def orderId=params.orderId
        rv(orderActivityService.orderActivity(orderId))
    }
}
