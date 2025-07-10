package com.tohid.url_shortener.controller

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class ShortenResponse(val shortenedUrl: String)
data class ShortenRequest(
    @field:NotBlank(message = "URL must not be blank")
    @field:URL(message = "Must be a valid URL")
    val originalUrl: String,
)

data class ResolveResponse(val originalUrl: String)
data class ErrorResponse(val error: String)