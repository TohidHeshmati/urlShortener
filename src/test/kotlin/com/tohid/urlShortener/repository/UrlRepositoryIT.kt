package com.tohid.urlShortener.repository

import com.tohid.urlShortener.domain.Url
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.Instant.now

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class UrlRepositoryIT(
    private val urlRepository: UrlRepository,
) {
    @Test
    fun `deletes existing url`() {
        val expiredUrl =
            urlRepository.save(
                Url(
                    shortUrl = "expired",
                    originalUrl = "http://example.com/expired",
                    expiryDate = now().minusSeconds(3600),
                ),
            )
        val nonExpiredUrl =
            urlRepository.save(
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
