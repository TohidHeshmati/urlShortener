package com.tohid.urlShortener.jobs

import com.tohid.urlShortener.repository.UrlRepository
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant.now

@Service
class UrlCleanupJob(
    private val urlRepository: UrlRepository,
) {
    @Scheduled(cron = "\${url.cleanup.cron}")
    @SchedulerLock(name = "cleanupExpiredUrls", lockAtMostFor = "5m", lockAtLeastFor = "30s")
    fun cleanupExpiredUrls() {
        val current = now()
        logger.info("Cleanup started for URLs expired before $current")

        val deleted = urlRepository.deleteByExpiryDateBefore(time = now())

        logger.info("Deleted $deleted expired URLs at $current")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UrlCleanupJob::class.java)
    }
}
