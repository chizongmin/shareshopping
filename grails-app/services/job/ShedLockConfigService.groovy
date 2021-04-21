package job

import com.mongodb.MongoClient
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean


class ShedLockConfigService {
    @Value('${grails.mongodb.databaseName}')
    String databaseName
    @Autowired
    protected MongoClient mongoClient

    @Bean
    LockProvider lockProvider() {
        return new MongoLockProvider(mongoClient.getDatabase(databaseName).getCollection("shedLock"))
    }
}
