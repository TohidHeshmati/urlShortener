package com.tohid.urlShortener.service

import com.tohid.urlShortener.controller.dtos.ResolveResponseDTO
import com.tohid.urlShortener.controller.dtos.ShortenRequestDTO
import com.tohid.urlShortener.controller.dtos.ShortenResponseDTO
import com.tohid.urlShortener.domain.Url
import com.tohid.urlShortener.domain.isExpired
import com.tohid.urlShortener.domain.toShortenResponseDTO
import com.tohid.urlShortener.exception.NotFoundException
import com.tohid.urlShortener.repository.UrlRepository
import com.tohid.urlShortener.utils.toBase62
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI

@Service
class UrlService(
    private val urlRepository: UrlRepository,
    private val redisIdGenerator: RedisIdGenerator,
) {
    fun shorten(request: ShortenRequestDTO): ShortenResponseDTO {
        val existing = urlRepository.findByOriginalUrl(request.originalUrl)
        if (existing != null) return existing.toShortenResponseDTO()

        val id = redisIdGenerator.nextId()
        val shortUrl = id.toBase62()
        val url =
            Url(
                originalUrl = request.originalUrl,
                shortUrl = shortUrl,
                expiryDate = request.expiryDate,
            )
        return urlRepository.save(url).toShortenResponseDTO()
    }

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

    fun redirecetByShortUrl(shortUrl: String): ResponseEntity<Void> {
        val url = resolve(shortUrl)
        val location = URI.create(url.originalUrl)
        val status =
            if (url.expiryDate != null) {
                HttpStatus.FOUND
            } else {
                HttpStatus.MOVED_PERMANENTLY
            }

        return ResponseEntity.status(status).location(location).build()
    }
}
