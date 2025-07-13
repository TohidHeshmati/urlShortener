package com.tohid.urlShortener.service

import com.tohid.urlShortener.controller.dtos.ShortenRequestDTO
import com.tohid.urlShortener.controller.dtos.ShortenResponseDTO
import com.tohid.urlShortener.domain.Url
import com.tohid.urlShortener.domain.toShortenResponseDTO
import com.tohid.urlShortener.repository.UrlRepository
import com.tohid.urlShortener.utils.toBase62
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI

@Service
class UrlService(
    private val urlRepository: UrlRepository,
    private val redisIdGenerator: RedisIdGenerator,
    private val urlResolverService: UrlResolverService,
) {
    fun shorten(request: ShortenRequestDTO): ShortenResponseDTO {
        val existing = urlResolverService.getByOriginalUrl(request.originalUrl)
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

    fun redirectsByShortUrl(shortUrl: String): ResponseEntity<Void> {
        val url = urlResolverService.resolve(shortUrl)
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
