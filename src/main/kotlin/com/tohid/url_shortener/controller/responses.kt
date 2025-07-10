package com.tohid.url_shortener.controller

data class ShortenResponse(val shortenedUrl: String)
data class ResolveResponse(val originalUrl: String)