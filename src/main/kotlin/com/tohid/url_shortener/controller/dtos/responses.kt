package com.tohid.url_shortener.controller.dtos

data class ResolveResponseDTO(val originalUrl: String)

data class ErrorResponseDTO(val error: String)