package background.address

import shareshopping.BaseController

class AddressController extends BaseController{
    def addressService
    def list() {
        def data=addressService.list()
        rv(data)
    }
    def upsert(){
        def map=request.getJSON()
        addressService.upsertAddress(map as HashMap)
        rv()
    }
    def updateSort(){
        def map=request.getJSON()
        addressService.updateSort(map as HashMap)
        rv()
    }
    def filterList() {
        rv(addressService.filterList())
    }
}
