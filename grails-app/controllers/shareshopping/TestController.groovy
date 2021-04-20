package shareshopping

import test.CyclicBarrierExampleService

class TestController extends BaseController{

    CyclicBarrierExampleService cyclicBarrierExampleService
    def test() {
        rv(cyclicBarrierExampleService.runTask())
    }
    def testDate(){
        rv([a:new Date()])
    }
}
