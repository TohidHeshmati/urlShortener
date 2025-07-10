package com.tohid.url_shortener.commands

import com.tohid.url_shortener.domain.Url
import com.tohid.url_shortener.repository.UrlRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class LocalDatabaseSeeder(
    private val urlRepository: UrlRepository
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        println("Seeding local database with initial URLs...")
        if (urlRepository.count() == 0L) {
            val generalUrls = listOf(
                Url(originalUrl = "https://www.google.com", shortUrl = "googl"),
                Url(originalUrl = "https://www.github.com", shortUrl = "github"),
                Url(originalUrl = "https://www.stackoverflow.com", shortUrl = "stackoverflow")
            )
            val randomUrls = (1..100).map { i ->
                Url(originalUrl = "https://example.com/page$i", shortUrl = "exam-$i")
            }
            urlRepository.saveAll(generalUrls + randomUrls)
        } else {
            println("Local database already seeded with URLs.")
        }
        println("Local database seeding completed. database contains ${urlRepository.count()} URLs.")
    }

}