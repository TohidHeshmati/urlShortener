package com.tohid.urlShortener.domain

import com.tohid.urlShortener.controller.dtos.ShortenResponseDTO
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.io.Serializable
import java.time.Instant
import java.time.Instant.now

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
    val createdAt: Instant = now(),
    @Column(name = "expiry_date", nullable = true)
    val expiryDate: Instant? = null,
) : Serializable

fun Url.toShortenResponseDTO() =
    ShortenResponseDTO(
        shortenedUrl = shortUrl,
        expiryDate = expiryDate,
    )

fun Url.isExpired(): Boolean = expiryDate?.let { now().isAfter(it) } ?: false
