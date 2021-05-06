package job

import mongo.ConfigService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import wx.WxService

class AccessTokenService {
    static lazyInit = false
    ConfigService configService
    WxService wxService
    @Scheduled(cron = '0 0 0/1 * * ?')
    @SchedulerLock(name = "accessToken")
    void taskRun() {
        def accessToken=wxService.fetchAccessToken()
        configService.updateById("accessToken",[accessToken:accessToken,times:new Date().time])
    }
}
