package com.tohid.urlShortener.service

import com.tohid.urlShortener.controller.dtos.ShortenRequestDTO
import com.tohid.urlShortener.controller.dtos.ShortenResponseDTO
import com.tohid.urlShortener.domain.Url
import com.tohid.urlShortener.repository.UrlRepository
import com.tohid.urlShortener.utils.toBase62
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class UrlServiceTest {
    private val urlRepository: UrlRepository = mock()
    private val redisIdGenerator: RedisIdGenerator = mock()
    private val urlResolverService: UrlResolverService = mock()
    private val urlService = UrlService(
        urlRepository = urlRepository,
        redisIdGenerator = redisIdGenerator,
        urlResolverService = urlResolverService
    )

    @Test
    fun `generates new shortened URL if original does not exist`() {
        val originalUrl = "https://example.com"
        val request = ShortenRequestDTO(originalUrl)
        val fakeId = 12345L
        val expectedShortUrl = fakeId.toBase62()

        whenever(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(null)
        whenever(redisIdGenerator.nextId()).thenReturn(fakeId)
        whenever(urlRepository.save(any())).thenAnswer { it.arguments[0] as Url }

        val response = urlService.shorten(request)

        assertEquals(ShortenResponseDTO(shortenedUrl = expectedShortUrl), response)
    }
}
