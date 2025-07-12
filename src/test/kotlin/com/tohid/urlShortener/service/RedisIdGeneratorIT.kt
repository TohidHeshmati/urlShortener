package com.tohid.urlShortener.service;

import com.tohid.urlShortener.BaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RedisIdGeneratorIT() : BaseIntegrationTest() {

    @Test
    fun `should increment ID each time`() {
        val first = redisIdGenerator.nextId()
        val second = redisIdGenerator.nextId()
        val third = redisIdGenerator.nextId()

        assertThat(second).isEqualTo(first + 1)
        assertThat(third).isEqualTo(second + 1)
    }
}
