package wx.goods

import shareshopping.BaseController

class WxGoodsBagController extends BaseController{
    def goodsBagService
    def save() {
        def token=request.getHeader("token")
        def map=request.getJSON() as HashMap
        goodsBagService.saveTobag(token,map)
        rv()
    }
    def list(){
        def token=request.getHeader("token")
        def data=goodsBagService.list(token)
        rv(data)
    }
    def delete(){
        def token=request.getHeader("token")
        def id=params.id
        goodsBagService.delete([token:token,id:id])
        rv()
    }
    def updateTotal(){
        def token=request.getHeader("token")
        def map=request.getJSON() as HashMap
        goodsBagService.updateTotal(token,map)
        rv()
    }
}
