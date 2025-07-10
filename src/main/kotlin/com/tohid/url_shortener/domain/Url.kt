package com.tohid.url_shortener.domain

import com.tohid.url_shortener.controller.ShortenResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "url")
data class Url(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "original_url", nullable = false, unique = true)
    val originalUrl: String,

    @Column(name = "short_url", nullable = false, unique = true)
    val shortUrl: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

fun Url.toShortenResponse() = ShortenResponse(shortenedUrl = shortUrl)