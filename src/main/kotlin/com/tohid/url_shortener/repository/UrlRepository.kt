package com.tohid.url_shortener.repository

import com.tohid.url_shortener.domain.Url
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
interface UrlRepository : CrudRepository<Url, Long> {
    fun findByShortUrl(shortUrl: String): Url?
    fun findByOriginalUrl(originalUrl: String): Url?

    @Transactional
    @Modifying
    @Query(
        "DELETE FROM Url u WHERE u.expiryDate IS NOT NULL AND u.expiryDate < :time",
    )
    fun deleteByExpiryDateBefore(time: Instant): Int
}