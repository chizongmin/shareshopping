package background.order

import shareshopping.BaseController

class OrderController  extends BaseController{
    def orderService
    def list() {
        def data=orderService.list(params)
        rv(data)
    }
}
