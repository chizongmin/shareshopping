package wx.goods

import shareshopping.BaseController

class WxGoodsController extends BaseController{
    def categoryService
    def tabList() {
        def data=categoryService.tabList()
        rv(data)
    }
    def tabMapGoods(){
        def data=categoryService.tabMapGoods()
        rv(data)
    }
}
