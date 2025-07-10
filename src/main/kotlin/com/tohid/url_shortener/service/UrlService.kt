package com.tohid.url_shortener.service

import com.tohid.url_shortener.domain.Url
import com.tohid.url_shortener.repository.UrlRepository
import org.springframework.stereotype.Service

@Service
class UrlService(
    private val urlRepository: UrlRepository
) {
        fun shorten(originalUrl: String): String {
        val existingUrl = urlRepository.findByOriginalUrl(originalUrl)
        if (existingUrl != null) {
            return existingUrl.shortUrl
        }

        val shortUrl = generateShortUrl(originalUrl)
        val url = Url(originalUrl = originalUrl, shortUrl = shortUrl)
        urlRepository.save(url)
        return shortUrl
    }

    fun resolve(shortUrl: String): String? =
        urlRepository.findByShortUrl(shortUrl)?.originalUrl

    fun generateShortUrl(originalUrl: String): String =
        originalUrl.hashCode().toString(36).take(8)
    // check existing URLs in the database
    // longer shorter url
}