package com.tohid.url_shortener.service

import com.tohid.url_shortener.controller.dtos.ResolveResponseDTO
import com.tohid.url_shortener.controller.dtos.ShortenRequestDTO
import com.tohid.url_shortener.controller.dtos.ShortenResponseDTO
import com.tohid.url_shortener.domain.Url
import com.tohid.url_shortener.domain.toShortenResponseDTO
import com.tohid.url_shortener.exception.NotFoundException
import com.tohid.url_shortener.repository.UrlRepository
import org.springframework.stereotype.Service

@Service
class UrlService(
    private val urlRepository: UrlRepository
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

    fun resolve(shortUrl: String): ResolveResponseDTO =
        ResolveResponseDTO(
            originalUrl = urlRepository.findByShortUrl(shortUrl)?.originalUrl ?: throw NotFoundException(
                "Short URL not found: $shortUrl"
            )
        )

    private fun generateShortUrl(shortenRequestDTO: ShortenRequestDTO) =
        Url(
            originalUrl = shortenRequestDTO.originalUrl,
            shortUrl = generateHash(shortenRequestDTO.originalUrl),
            expiryDate = shortenRequestDTO.expiryDate
        )

    private fun generateHash(originalUrl: String): String = originalUrl.hashCode().toString(36).take(8)
    // check existing URLs in the database
    // longer shorter url
}