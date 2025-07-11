package com.tohid.url_shortener.controller.dtos

import java.time.Instant

data class ResolveResponseDTO(
    val originalUrl: String,
    val expiryDate: Instant? = null,
    )

data class ErrorResponseDTO(val error: String)