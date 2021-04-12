package test

import java.util.concurrent.BrokenBarrierException
import java.util.concurrent.CyclicBarrier

class CyclicBarrierTaskService implements Runnable{

    private CyclicBarrier barrier;
    private orderNumberService
    public CyclicBarrierTaskService(barrier,orderNumberService) {
        this.barrier = barrier;
        this.orderNumberService=orderNumberService
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " 准备就绪");
            barrier.await();
            def data=this.getNumber()
            System.out.println(Thread.currentThread().getName() + " 获取--${data}");
        } catch (InterruptedException ex) {
        } catch (BrokenBarrierException ex) {
        }
    }
    def getNumber(){
        def data=orderNumberService.created()
        return data
    }
}
