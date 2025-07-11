package com.tohid.urlShortener.jobs

import com.tohid.urlShortener.repository.UrlRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant.now

@Service
class UrlCleanupJob(
    private val urlRepository: UrlRepository,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UrlCleanupJob::class.java)
    }

    @Scheduled(cron = "0 0 2 * * *") // Runs every day at 2 AM
    fun cleanupExpiredUrls() {
        val now = now()
        logger.info("Cleaning up expired urls before $now.... Starting time: ${System.currentTimeMillis()}")
        val deleted = urlRepository.deleteByExpiryDateBefore(time = now)
        logger.info("Deleted $deleted expired urls. Finished time: ${System.currentTimeMillis()}")
    }
}
