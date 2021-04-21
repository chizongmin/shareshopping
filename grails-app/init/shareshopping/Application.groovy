package shareshopping

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

import groovy.transform.CompileStatic
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
//defaultLockAtMostFor 指定在执行节点死亡时应将锁保留多长时间,作用就是在被加锁的节点挂了时，无法释放锁，造成其他节点无法进行下一任务
//defaultLockAtLeastFor保留锁定的最短时间
@CompileStatic
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT1M", defaultLockAtLeastFor = "PT10S")
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
