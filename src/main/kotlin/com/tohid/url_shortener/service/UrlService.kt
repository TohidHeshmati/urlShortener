package com.tohid.url_shortener.service

import com.tohid.url_shortener.controller.ResolveResponse
import com.tohid.url_shortener.controller.ShortenRequest
import com.tohid.url_shortener.controller.ShortenResponse
import com.tohid.url_shortener.domain.Url
import com.tohid.url_shortener.domain.toShortenResponse
import com.tohid.url_shortener.exception.NotFoundException
import com.tohid.url_shortener.repository.UrlRepository
import org.springframework.stereotype.Service

@Service
class UrlService(
    private val urlRepository: UrlRepository
) {
    fun shorten(shortenRequest: ShortenRequest): ShortenResponse {
        val existingUrl = urlRepository.findByOriginalUrl(shortenRequest.originalUrl)
        return if (existingUrl != null) {
            existingUrl.toShortenResponse()
        } else {
            val shortUrl = generateShortUrl(shortenRequest)
            val result = urlRepository.save(shortUrl)
            result.toShortenResponse()
        }
    }

    fun resolve(shortUrl: String): ResolveResponse =
        ResolveResponse(
            originalUrl = urlRepository.findByShortUrl(shortUrl)?.originalUrl ?: throw NotFoundException(
                "Short URL not found: $shortUrl"
            )
        )

    private fun generateShortUrl(shortenRequest: ShortenRequest) = Url(
        originalUrl = shortenRequest.originalUrl, shortUrl = generateHash(shortenRequest.originalUrl)
    )

    private fun generateHash(originalUrl: String): String = originalUrl.hashCode().toString(36).take(8)
    // check existing URLs in the database
    // longer shorter url
}