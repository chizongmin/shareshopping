package job

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import order.OrderService
import org.apache.commons.lang3.time.DateUtils
import org.springframework.scheduling.annotation.Scheduled
import static groovyx.gpars.GParsExecutorsPool.withPool
class OrderAutoCompleteService {
    static lazyInit = false
    static int autoCompleteDays=7
    OrderService orderService
    @Scheduled(cron = '0 0/1 * * * ?')
    @SchedulerLock(name = "orderAutoComplete")
    void taksRun() {
        def orderList=orderService.findAll([status:"WAIT_CONFIRM"],["id","status","dateCreated"])
        withPool(3) {
            orderList.eachParallel { order ->
                try {
                    def autoTime= DateUtils.addDays(order.dateCreated,autoCompleteDays)
                    if(new Date().after(autoTime)){ //到期
                        def data=orderService.updateStatus("",[order.id,"COMPLETED"])
                        log.info("自动完成订单：${order.id},complete order:${data?.id}")
                    }
                } catch (Exception e) {
                    log.error("订单：${order.id} auto complete error",e)
                }
            }
        }
    }
}
