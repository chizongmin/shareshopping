package background.goods

import shareshopping.BaseController

class CategoryController extends BaseController{
    def categoryService
    def editList() {
        def data=categoryService.editList()
        rv(data)
    }
    def updateInfo(){
        def params=request.getJSON() as HashMap
        categoryService.updateInfo(params)
        rv()
    }
}
