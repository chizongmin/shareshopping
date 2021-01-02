package background.user

import shareshopping.BaseController

class UserAddressController extends BaseController{
    def userAddressService
    def filterList() {
        rv(userAddressService.filterList())
    }
}
