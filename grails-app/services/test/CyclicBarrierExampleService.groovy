package test
import java.util.concurrent.CyclicBarrier

class CyclicBarrierExampleService {
    def  orderNumberService
    def runTask(){
        final CyclicBarrier cb = new CyclicBarrier(10, new Runnable(){
            @Override
            public void run(){
                //一旦所有线程准备就绪，这个动作就执行
                System.out.println("准备就绪，开始！");
            }
        });

        for(int i = 1;i<=10;i++) {
            Thread  sporter= new Thread(new CyclicBarrierTaskService(cb,orderNumberService) ,i+"号选手");
            sporter.start();
        }
    }

}
