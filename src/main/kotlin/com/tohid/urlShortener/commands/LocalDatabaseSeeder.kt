package com.tohid.urlShortener.commands

import com.tohid.urlShortener.domain.Url
import com.tohid.urlShortener.repository.UrlRepository
import com.tohid.urlShortener.utils.toBase62
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
/* * LocalDatabaseSeeder is a CommandLineRunner that seeds the local database with initial URLs.
 * It runs only when the application is started with the "local" profile.
 * This is useful for development and testing purposes to have a set of predefined URLs.
 */

@Profile("local")
@Component
class LocalDatabaseSeeder(
    private val urlRepository: UrlRepository,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        println("Seeding local database with initial URLs...")
        if (urlRepository.count() == 0L) {
            val generalUrls =
                listOf(
                    Url(originalUrl = "https://www.google.com", shortUrl = 246L.toBase62()),
                    Url(originalUrl = "https://www.github.com", shortUrl = 247L.toBase62()),
                    Url(originalUrl = "https://www.stackoverflow.com", shortUrl = 248L.toBase62()),
                )
            val randomUrls =
                (1..100).map { i ->
                    Url(originalUrl = "https://example.com/page$i", shortUrl = "exam-$i")
                }
            urlRepository.saveAll(generalUrls + randomUrls)
        } else {
            println("Local database already seeded with URLs.")
        }
        println("Local database seeding completed. database contains ${urlRepository.count()} URLs.")
    }
}
