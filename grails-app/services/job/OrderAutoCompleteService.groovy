package job

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled

class OrderAutoCompleteService {
    static lazyInit = false

    @Scheduled(cron = '0/1 * * * * ?')
    @SchedulerLock(name = "orderAutoComplete")
    void taksRun() {
        log.info("1111111")
    }
}
