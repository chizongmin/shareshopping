package shareshopping

import test.CyclicBarrierExampleService

class TestController extends BaseController{
    def orderNumberService
    CyclicBarrierExampleService cyclicBarrierExampleService
    def test() {
        rv(cyclicBarrierExampleService.runTask())
    }
}
