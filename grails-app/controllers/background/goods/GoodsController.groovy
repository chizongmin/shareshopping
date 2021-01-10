package background.goods

import shareshopping.BaseController

class GoodsController extends BaseController{
    def goodsService
    def editList() {
        def data=goodsService.editList(params.category,params.search)
        rv(data)
    }
    def upsert(){
        def map=request.getJSON()
        goodsService.upsertGoods(params.uid,map as HashMap)
        rv()
    }
    def delete(){
        goodsService.deleteGoods(params.id)
        rv()
    }
}
