package com.tohid.urlShortener.controller.dtos

import java.io.Serializable
import java.time.Instant

data class ResolveResponseDTO(
    val originalUrl: String,
    val expiryDate: Instant? = null,
): Serializable

data class ErrorResponseDTO(val error: String)
