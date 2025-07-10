package com.tohid.url_shortener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tohid.url_shortener.domain.Url
import com.tohid.url_shortener.repository.UrlRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainControllerIT(
    private val restTemplate: TestRestTemplate,
    private val urlRepository: UrlRepository,
    @LocalServerPort private val port: Int,
    private val objectMapper: ObjectMapper
) {

    val baseUrl = "http://localhost:$port"

    @BeforeEach
    fun cleanDB() {
        urlRepository.deleteAll()
    }

    @Test
    fun `should resolve shortened URL`() {
        val savedUrl = urlRepository.save(Url(originalUrl = "http://example.com/1", shortUrl = "abc001"))

        val response: ResponseEntity<String> = restTemplate.exchange(
            "$baseUrl/abc001", HttpMethod.GET, null, String::class.java
        )


        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = objectMapper.readValue(response.body, ResolveResponse::class.java)
        assertThat(body.originalUrl).isEqualTo(savedUrl.originalUrl)
    }

    @Test
    fun `should return 404 when short URL not found`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "$baseUrl/nonexistent", HttpMethod.GET, null, String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        val body = objectMapper.readValue(response.body, ResolveResponse::class.java)
        assertThat(body.originalUrl).isEqualTo("Not Found")
    }

    @Test
    fun `should shorten a new URL`() {
        val response = restTemplate.getForObject(
            "$baseUrl/shorten?originalUrl=http://newsite.com", String::class.java
        )

        val shortenResponse = objectMapper.readValue(response, ShortenResponse::class.java)

        assertThat(response).isNotNull
    }
}