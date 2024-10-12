package com.searcher.mapsearcher.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.api.RedissonReactiveClient
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer

@Configuration
class RedisConfig(
    @Value("\${spring.redis.port}") private val redisPort: Int
) {
    private lateinit var redisServer: RedisServer

    @PostConstruct
    fun startRedis() {
        redisServer = RedisServer(redisPort)
        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config().apply {
            codec = StringCodec(Charsets.UTF_8)
            useSingleServer().apply {
                address = "redis://localhost:$redisPort"
                connectTimeout = 1000 * 5 // 연결 timeout
                timeout = 1000 * 3 // redis 명령어 대기 timeout
                idleConnectionTimeout = 1000 * 60 // 유후 연결 connection 에 대한 timeout
                clientName = "map-searcher"
            }
        }
        return Redisson.create(config)
    }

    @Bean
    fun reactiveRedissonClient(redissonClient: RedissonClient): RedissonReactiveClient {
        return redissonClient.reactive()
    }
}