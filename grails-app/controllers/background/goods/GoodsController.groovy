package background.goods

import shareshopping.BaseController

class GoodsController extends BaseController{
    def goodsService
    def editList() {
        def data=goodsService.editList()
        rv(data)
    }
}
