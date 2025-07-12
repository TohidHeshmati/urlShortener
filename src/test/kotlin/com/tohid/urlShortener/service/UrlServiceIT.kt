package com.tohid.urlShortener.service

import com.tohid.urlShortener.BaseIntegrationTest
import com.tohid.urlShortener.domain.Url
import com.tohid.urlShortener.exception.NotFoundException
import com.tohid.urlShortener.utils.toBase62
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import kotlin.test.assertEquals

class UrlServiceIT() : BaseIntegrationTest() {

    @Test
    fun `resolves Url from cache if it exists and cache is hit`() {
        val originalUrl = "https://example.com"
        val fakeId = 12345L
        val expectedShortUrl = fakeId.toBase62()
        urlRepository.save(
            Url(
                originalUrl = originalUrl,
                shortUrl = expectedShortUrl,
            )
        )

        val firsResolveFromDB = urlService.resolve(expectedShortUrl)
        assert(firsResolveFromDB.originalUrl == originalUrl)

        urlRepository.deleteAll()
        assert(urlRepository.count() == 0L)

        val secondResolveFromCache = urlService.resolve(expectedShortUrl)
        assert(secondResolveFromCache.originalUrl == originalUrl)
    }

    @Test
    fun `resolved expired Url is deleted from cache and db`() {
        val originalUrl = "https://expired.com"
        val shortUrl = 54321L.toBase62()
        val expiredUrl = urlRepository.save(
            Url(
                originalUrl = originalUrl,
                shortUrl = shortUrl,
                expiryDate = Instant.now().minusSeconds(60) // 1 min ago
            )
        )

        val exception = assertThrows<NotFoundException> {
            urlService.resolve(shortUrl)
        }
        assertEquals("Short URL has expired: $shortUrl", exception.message)

        assert(urlRepository.count() == 0L)
        val cached = redisTemplate.opsForValue().get("short:$shortUrl")
        assert(cached == null)
    }
}
