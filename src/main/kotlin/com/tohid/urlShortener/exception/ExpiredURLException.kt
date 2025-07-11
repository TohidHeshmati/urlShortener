package com.tohid.urlShortener.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
data class ExpiredURLException(val originalUrl: String) :
    RuntimeException("This URL is expired. $originalUrl")
