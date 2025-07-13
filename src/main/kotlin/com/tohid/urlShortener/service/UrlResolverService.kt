package com.tohid.urlShortener.service

import com.tohid.urlShortener.controller.dtos.ResolveResponseDTO
import com.tohid.urlShortener.domain.Url
import com.tohid.urlShortener.domain.isExpired
import com.tohid.urlShortener.exception.NotFoundException
import com.tohid.urlShortener.repository.UrlRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class UrlResolverService(
    private val urlRepository: UrlRepository
) {

    @Cacheable(cacheNames = ["short-urls"], key = "#shortUrl")
    fun resolve(shortUrl: String): ResolveResponseDTO {
        val url =
            urlRepository.findByShortUrl(shortUrl)
                ?: throw NotFoundException("Short URL not found: $shortUrl")

        if (url.isExpired()) {
            urlRepository.delete(url)
            throw NotFoundException("Short URL has expired: $shortUrl")
        }

        return ResolveResponseDTO(
            originalUrl = url.originalUrl,
            expiryDate = url.expiryDate,
        )
    }


    @Cacheable(cacheNames = ["original-urls"], key = "#originalUrl")
    fun getByOriginalUrl(originalUrl: String): Url? {
        return urlRepository.findByOriginalUrl(originalUrl)
    }
}
