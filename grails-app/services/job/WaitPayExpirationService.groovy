package job

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled


class WaitPayExpirationService {
    static lazyInit = false

    @Scheduled(cron = '0/1 * * * * ?')
    @SchedulerLock(name = "payExpiration")
    void taksRun() {
        log.info("1111111")
    }
}
