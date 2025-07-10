package com.tohid.url_shortener.repository

import com.tohid.url_shortener.domain.Url
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlRepository : CrudRepository<Url, Long> {
    fun findUrlByShortUrl(shortUrl: String): Url?
}