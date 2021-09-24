package job

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import order.OrderService
import org.springframework.scheduling.annotation.Scheduled


class WaitPayExpirationService {
    static lazyInit = false
    OrderService orderService
    @Scheduled(cron = '0 0/1 * * * ?')
    @SchedulerLock(name = "waitPayExpiration")
    void taskRun() {
        def orderList=orderService.findAll([status:"WAIT_PAY",payExpirationTime:['$lt':new Date()]])
        orderList.each{order->
            try {
                def data=orderService.orderCancel(order.id,null)
                log.info("取消订单：${order.id},complete order:${data?.id}")
            } catch (Exception e) {
                log.error("订单：${order.id} cancel error",e)
            }
        }
    }
}
