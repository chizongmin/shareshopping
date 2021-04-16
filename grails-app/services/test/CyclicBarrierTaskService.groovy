package test

import java.util.concurrent.BrokenBarrierException
import java.util.concurrent.CyclicBarrier

class CyclicBarrierTaskService implements Runnable{

    private CyclicBarrier barrier;
    private testService
    public CyclicBarrierTaskService(barrier,testService) {
        this.barrier = barrier;
        this.testService=testService
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " 准备就绪");
            barrier.await();
            def data=this.test()
            System.out.println(Thread.currentThread().getName() + " 获取--${data}");
        } catch (InterruptedException ex) {
        } catch (BrokenBarrierException ex) {
        }
    }
    def test(){
        def data=testService.test()
        return data
    }
}
