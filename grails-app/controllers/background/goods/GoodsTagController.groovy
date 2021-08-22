package background.goods

import goods.GoodsTagService
import shareshopping.BaseController

class GoodsTagController extends BaseController{
    GoodsTagService goodsTagService
    def add() {
        def result=goodsTagService.addTag(request.getJSON().name)
        rv(result.data,result.code,result.message)
    }
    def nameLike(){
        rv(goodsTagService.nameLike(params.keyword))
    }
}
