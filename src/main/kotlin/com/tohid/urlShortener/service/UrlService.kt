package com.tohid.urlShortener.service

import com.tohid.urlShortener.controller.dtos.ResolveResponseDTO
import com.tohid.urlShortener.controller.dtos.ShortenRequestDTO
import com.tohid.urlShortener.controller.dtos.ShortenResponseDTO
import com.tohid.urlShortener.domain.Url
import com.tohid.urlShortener.domain.isExpired
import com.tohid.urlShortener.domain.toShortenResponseDTO
import com.tohid.urlShortener.exception.NotFoundException
import com.tohid.urlShortener.repository.UrlRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI

@Service
class UrlService(
    private val urlRepository: UrlRepository,
) {
    fun shorten(shortenRequestDTO: ShortenRequestDTO): ShortenResponseDTO {
        val existingUrl = urlRepository.findByOriginalUrl(shortenRequestDTO.originalUrl)
        return if (existingUrl != null) {
            existingUrl.toShortenResponseDTO()
        } else {
            val shortUrl = generateShortUrl(shortenRequestDTO)
            val result = urlRepository.save(shortUrl)
            result.toShortenResponseDTO()
        }
    }

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

    fun findUrlByShortUrl(shortUrl: String): ResponseEntity<Void> {
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

    private fun generateShortUrl(shortenRequestDTO: ShortenRequestDTO) =
        Url(
            originalUrl = shortenRequestDTO.originalUrl,
            shortUrl = generateHash(shortenRequestDTO.originalUrl),
            expiryDate = shortenRequestDTO.expiryDate,
        )

    private fun generateHash(originalUrl: String): String = originalUrl.hashCode().toString(36).take(8)
    // check existing URLs in the database
    // longer shorter url
}
