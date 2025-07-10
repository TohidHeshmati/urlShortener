package com.tohid.url_shortener.service

import com.tohid.url_shortener.repository.UrlRepository
import org.springframework.stereotype.Service

@Service
class UrlService(
    private val urlRepository: UrlRepository
) {
    fun findUrlByShortUrl(shortUrl: String) = urlRepository.findUrlByShortUrl(shortUrl)
    fun findAll() = urlRepository.findAll()
}