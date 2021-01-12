package background.goods

import shareshopping.BaseController

class CategoryController extends BaseController{
    def categoryService
    def editList() {
        def data=categoryService.editList(params)
        rv(data)
    }
    def updateInfo(){
        def params=request.getJSON() as HashMap
        categoryService.updateInfo(params)
        rv()
    }
    def updateSort(){
        def params=request.getJSON() as HashMap
        categoryService.updateSort(params)
        rv()
    }
    def addGoods(){
        def params=request.getJSON() as HashMap
        categoryService.addGoods(params)
        rv()
    }
    def updateGoodsSort(){
        def params=request.getJSON() as HashMap
        categoryService.updateGoodsSort(params)
        rv()
    }
    def deleteGoods(){
        categoryService.deleteGoods(params)
        rv()
    }
}
