package com.tohid.urlShortener.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisIdGenerator(private val redisTemplate: StringRedisTemplate) {

    fun nextId(): Long = redisTemplate.opsForValue().increment(COUNTER_KEY)!!

    companion object {
        private const val COUNTER_KEY = "url:global:counter"
    }
}
