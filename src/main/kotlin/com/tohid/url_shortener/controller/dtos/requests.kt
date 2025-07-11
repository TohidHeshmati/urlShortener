package com.tohid.url_shortener.controller.dtos

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.tohid.url_shortener.utils.SafeInstantDeserializer
import com.tohid.url_shortener.validators.FutureInstant
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import java.time.Instant

data class ShortenResponseDTO(
    val shortenedUrl: String,
    val expiryDate: Instant? = null
)

data class ShortenRequestDTO(
    @field:NotBlank(message = "URL must not be blank")
    @field:URL(message = "Must be a valid URL")
    val originalUrl: String,

    @field:FutureInstant
    @field:JsonDeserialize(using = SafeInstantDeserializer::class)
    val expiryDate: Instant? = null
)
