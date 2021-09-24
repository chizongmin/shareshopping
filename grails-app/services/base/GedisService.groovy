package base
import grails.plugins.redis.RedisService
import redis.clients.jedis.Jedis
class GedisService {

    RedisService redisService

    def exists(String key) {
        assert key
        redisService.withRedis { Jedis jedis ->
            jedis.exists(key)
        }
    }

    def set(String key, String value) {
        assert key
        assert value
        redisService."$key" = value
    }

    String get(String key, boolean burnAfterReading = false) {
        assert key
        redisService.withRedis { Jedis jedis ->
            def result = jedis.get(key)
            if (burnAfterReading) {
                jedis.del(key)
            }
            return result
        }
    }

    def hmset(String key, Map<String, String> valueMap) {
        assert key
        assert valueMap
        redisService.withRedis { Jedis jedis ->
            jedis.hmset(key, valueMap)
        }
    }

    def hmget(String key, boolean burnAfterReading = false) {
        assert key
        redisService.withRedis { Jedis jedis ->
            def result = jedis.hgetAll(key)
            if (burnAfterReading || result.burnAfterReading == 'Y') {
                jedis.del(key)
            }
            return result
        }
    }

    def memoize(String key, String value, int expireSeconds) {
        assert expireSeconds > 0
        assert key
        assert value
        redisService.withRedis { Jedis jedis ->
            jedis.setex(key, expireSeconds, value)
        }
    }

    def updateMemoizedValue(String key, String value) {
        assert key
        assert value
        redisService.withRedis { Jedis jedis ->
            Long remainingExp = jedis.ttl(key)
            if (remainingExp == RedisService.KEY_DOES_NOT_EXIST || remainingExp == RedisService.NO_EXPIRATION_TTL) {
                log.warn("${key} not exists or ttl not set, ignore the operation of updating to ${value}")
                return remainingExp
            }
            jedis.setex(key, remainingExp.intValue(), value)
        }
    }

    def memoizeHash(String key, Map<String, String> valueMap, int expireSeconds) {
        assert expireSeconds > 0
        assert key
        assert valueMap
        redisService.withRedis { Jedis jedis ->
            jedis.hmset(key, valueMap)
            jedis.expire(key, expireSeconds)
        }
    }

    def updateMemoizedHash(String key, Map<String, String> valueMap) {
        assert key
        assert valueMap
        redisService.withRedis { Jedis jedis ->
            Long remainingExp = jedis.ttl(key)
            if (remainingExp == RedisService.KEY_DOES_NOT_EXIST || remainingExp == RedisService.NO_EXPIRATION_TTL) {
                return remainingExp
            }
            jedis.hmset(key, valueMap)
            jedis.expire(key, remainingExp.intValue())
        }
    }

    def ttl(String key) {
        assert key
        redisService.withRedis { Jedis jedis ->
            jedis.ttl(key)
        }
    }

    def del(String key) {
        assert key
        redisService.withRedis { Jedis jedis ->
            jedis.del(key)
        }
    }

    def setnx(String key, String value) {
        assert key
        assert value
        redisService.withRedis { Jedis jedis ->
            jedis.setnx(key, value)
        }
    }

    def expire(String key, int expSeconds) {
        assert key
        assert expSeconds > 0
        del(key)
        redisService.withRedis { Jedis jedis ->
            jedis.expire(key, expSeconds)
        }
    }
}
