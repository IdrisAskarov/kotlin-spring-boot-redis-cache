package com.codergm.cachetutorial.config

import com.codergm.cachetutorial.dto.ProductDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

@Configuration
class RedisConfig {

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(serializerFor<ProductDto>(jacksonObjectMapper()))

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(redisCacheConfiguration)
            .build()
    }

    private inline fun <reified T> serializerFor(objectMapper: ObjectMapper): RedisSerializationContext.SerializationPair<T> {
        val serializer = Jackson2JsonRedisSerializer(objectMapper, T::class.java)
        return RedisSerializationContext.SerializationPair.fromSerializer(serializer)
    }
}