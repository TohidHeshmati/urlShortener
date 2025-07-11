package com.tohid.url_shortener.jobs

import com.tohid.url_shortener.repository.UrlRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant.now

@Service
class UrlCleanupJob(
    private val urlRepository: UrlRepository
) {

    @Scheduled(cron = "0 0 2 * * *") // Runs every day at 2 AM
    fun cleanupExpiredUrls() {
        val now = now()
        // TODO(): "Logg the cleanup operation"
        val deleted = urlRepository.deleteByExpiryDateBefore(time = now)
        // TODO(): "Logg the cleanup operation results"
    }
}