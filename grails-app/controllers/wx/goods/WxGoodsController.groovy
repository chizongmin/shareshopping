package wx.goods

import goods.CategoryService
import goods.GoodsService
import shareshopping.BaseController

class WxGoodsController extends BaseController{
    CategoryService categoryService
    GoodsService goodsService
    def tabList() {
        def data=categoryService.tabList()
        rv(data)
    }
    def tabMapGoods(){
        def data=categoryService.tabMapGoods()
        rv(data)
    }
    def findById(){
        def id=params.id
        rv(goodsService.selectByIdWithCache(id))
    }
    def findByIds(){
        def map=request.getJSON() as HashMap
        rv(goodsService.selectByIds(map.ids))
    }
    def confirmGoods(){
        def token=request.getHeader("token")
        def map=request.getJSON() as HashMap
        rv(goodsService.confirmGoods(token,map.ids))
    }
}
