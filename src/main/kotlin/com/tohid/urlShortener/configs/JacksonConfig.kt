package com.tohid.urlShortener.configs

import com.fasterxml.jackson.databind.module.SimpleModule
import com.tohid.urlShortener.utils.SafeInstantDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant
import kotlin.jvm.java

@Configuration
class JacksonConfig {
    @Bean
    fun customInstantModule(): SimpleModule =
        SimpleModule().addDeserializer(
            Instant::class.java,
            SafeInstantDeserializer(),
        )
}
