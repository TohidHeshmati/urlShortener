package com.tohid.urlShortener.repository

import com.tohid.urlShortener.BaseIntegrationTest
import com.tohid.urlShortener.domain.Url
import com.tohid.urlShortener.utils.toBase62
import makeUrl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant.now

class UrlRepositoryIT() : BaseIntegrationTest() {

    private val originalBase = "http://example.com/something/long/"

    @Test
    fun `findByShortUrl returns correct Url if exists`() {
        val shortUrlToBeFound = 453L.toBase62()
        val shortUrlNotToBeFound = 454L.toBase62()
        urlRepository.saveAll(
            listOf(
                makeUrl(originalUrl = "$originalBase/1", shortUrl = shortUrlToBeFound),
                makeUrl(originalUrl = "$originalBase/2", shortUrl = shortUrlNotToBeFound)
            )
        )

        val found = urlRepository.findByShortUrl(shortUrlToBeFound)

        assertThat(found).isNotNull
        assertThat(found!!.shortUrl).isEqualTo(shortUrlToBeFound)
        assertThat(found.shortUrl).isNotEqualTo(shortUrlNotToBeFound)
    }

    @Test
    fun `findByOriginalUrl returns correct Url if exists`() {
        val shortUrlToBeFound = 846L.toBase62()
        val originalUrlToBeFound = "$originalBase/$shortUrlToBeFound"
        val shortUrlNotToBeFound = 847L.toBase62()
        val originalUrlNotToBeFound = "$originalBase/$shortUrlNotToBeFound"
        urlRepository.saveAll(
            listOf(
                makeUrl(
                    shortUrl = 846L.toBase62(),
                    originalUrl = originalUrlToBeFound,
                ), makeUrl(
                    shortUrl = shortUrlNotToBeFound,
                    originalUrl = originalUrlNotToBeFound,
                )
            )
        )

        val found = urlRepository.findByOriginalUrl(originalUrlToBeFound)

        assertThat(found).isNotNull
        assertThat(found!!.shortUrl).isEqualTo(shortUrlToBeFound)
        assertThat(found.originalUrl).isEqualTo(originalUrlToBeFound)
    }

    @Test
    fun `deletes expired urls`() {
        val expiredUrl = urlRepository.save(
            Url(
                shortUrl = "expired",
                originalUrl = "http://example.com/expired",
                expiryDate = now().minusSeconds(3600),
            ),
        )
        val nonExpiredUrl = urlRepository.save(
            Url(
                shortUrl = "non-expired",
                originalUrl = "http://example.com/non-expired",
                expiryDate = now().plusSeconds(3600),
            ),
        )

        val deletedCount = urlRepository.deleteByExpiryDateBefore(time = now())

        assertEquals(1, deletedCount)
        assertThat(urlRepository.findByShortUrl(expiredUrl.shortUrl)).isNull()
        assertThat(urlRepository.findByShortUrl(nonExpiredUrl.shortUrl)).isNotNull
    }
}
