package com.tohid.url_shortener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tohid.url_shortener.controller.dtos.ErrorResponseDTO
import com.tohid.url_shortener.controller.dtos.ResolveResponseDTO
import com.tohid.url_shortener.controller.dtos.ShortenRequestDTO
import com.tohid.url_shortener.controller.dtos.ShortenResponseDTO
import com.tohid.url_shortener.domain.Url
import com.tohid.url_shortener.repository.UrlRepository
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.TestConstructor
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.time.Instant
import java.time.Instant.now

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainControllerIT(
    private val restTemplate: TestRestTemplate,
    private val urlRepository: UrlRepository,
    @LocalServerPort private val port: Int,
    private val objectMapper: ObjectMapper
) {

    private val baseUrl = "http://localhost:$port"

    private val headers: HttpHeaders = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
    }

    private val redirectSafeRestTemplate = RestTemplate(
        HttpComponentsClientHttpRequestFactory().apply {
            httpClient = HttpClients.custom()
                .disableRedirectHandling()
                .build()
        }
    )


    @BeforeEach
    fun cleanDB() {
        urlRepository.deleteAll()
    }

    @Test
    fun `should shorten a valid URL`() {
        val entity = HttpEntity(
            ShortenRequestDTO(originalUrl = "https://www.example.com"), headers
        )

        val response: ResponseEntity<ShortenResponseDTO> = restTemplate.postForEntity(
            "$baseUrl/", entity, ShortenResponseDTO::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        assertThat(response.body!!.shortenedUrl).isNotBlank
    }

    @Test
    fun `should resolve shortened URL`() {
        val savedUrl = urlRepository.save(Url(originalUrl = "http://example.com/1", shortUrl = "abc001"))

        val response: ResponseEntity<String> = restTemplate.exchange(
            "$baseUrl/resolve/abc001", HttpMethod.GET, null, String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = objectMapper.readValue(response.body, ResolveResponseDTO::class.java)
        assertThat(body.originalUrl).isEqualTo(savedUrl.originalUrl)
    }

    @Test
    fun `redirects permanently for shortened URL without expiry date`() {
        val savedUrl = urlRepository.save(Url(originalUrl = "http://example.com/1", shortUrl = "abc001"))

        val response: ResponseEntity<String> = redirectSafeRestTemplate.exchange(
            "$baseUrl/abc001", HttpMethod.GET, null, String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.MOVED_PERMANENTLY)
        assertThat(response.headers.location).isEqualTo(URI.create(savedUrl.originalUrl))
    }

    @Test
    fun `redirects temporary for shortened URL with expiry date in future`() {
        val savedUrl = urlRepository.save(
            Url(
                originalUrl = "http://example.com/1",
                shortUrl = "abc001",
                expiryDate = now().plusSeconds(3600)
            )
        )

        val response: ResponseEntity<String> = redirectSafeRestTemplate.exchange(
            "$baseUrl/abc001", HttpMethod.GET, null, String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.FOUND)
        assertThat(response.headers.location).isEqualTo(URI.create(savedUrl.originalUrl))
    }

    @Test
    fun `returns not found for shortened URL with expiry date in past`() {
        urlRepository.save(
            Url(
                originalUrl = "http://example.com/1",
                shortUrl = "abc001",
                expiryDate = now().minusSeconds(3600)
            )
        )

        val response: ResponseEntity<String> = restTemplate.exchange(
            "$baseUrl/abc001", HttpMethod.GET, null, String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

        val body = objectMapper.readValue(response.body, ErrorResponseDTO::class.java)
        assertThat(body).isEqualTo(ErrorResponseDTO("Short URL has expired: abc001"))
    }

    @Test
    fun `removes the url from db when requested and expiry date in past`() {
        urlRepository.save(
            Url(
                originalUrl = "http://example.com/1",
                shortUrl = "abc001",
                expiryDate = now().minusSeconds(3600)
            )
        )

        val response: ResponseEntity<String> = restTemplate.exchange(
            "$baseUrl/abc001", HttpMethod.GET, null, String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

        val body = objectMapper.readValue(response.body, ErrorResponseDTO::class.java)
        assertThat(body).isEqualTo(ErrorResponseDTO("Short URL has expired: abc001"))
        assertThat(urlRepository.findByShortUrl("abc001")).isNull()
    }

    @Test
    fun `should return 404 for non-existent short URL`() {
        val response = restTemplate.getForEntity("$baseUrl/unknown123", String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        val error = objectMapper.readTree(response.body)
        assertThat(error["error"].asText()).contains("Short URL not found")
    }

    @Nested
    inner class ValidationTests {

        @Test
        fun `should return 400 for blank URL input`() {
            val request = ShortenRequestDTO(originalUrl = "")

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<ErrorResponseDTO> = restTemplate.postForEntity(
                "$baseUrl/", entity, ErrorResponseDTO::class.java
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(response.body).isEqualTo(ErrorResponseDTO(error = "originalUrl: URL must not be blank"))
        }

        @Test
        fun `should return 400 for invalid URL input`() {
            val request = ShortenRequestDTO(originalUrl = "not_a_url")

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<ErrorResponseDTO> = restTemplate.postForEntity(
                "$baseUrl/", entity, ErrorResponseDTO::class.java
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(response.body).isEqualTo(ErrorResponseDTO(error = "originalUrl: Must be a valid URL"))
        }

        @ParameterizedTest
        @ValueSource(
            strings =
                [
                    "2000-01-01T00:00:00Z",
                    "2023-10-01T00:00:00Z",
                ]
        )
        fun `should return 400 for expired expiryDate`(timeInPast: String) {
            val request = mapOf(
                "original_url" to "https://example.com",
                "expiry_date" to timeInPast
            )

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<ErrorResponseDTO> = restTemplate.postForEntity(
                "$baseUrl/", entity, ErrorResponseDTO::class.java
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(response.body?.error).contains("expiryDate: The date must be in the future")
        }

        @Test
        fun `should return 201 for valid future expiryDate`() {
            val request = mapOf(
                "original_url" to "https://example.com",
                "expiry_date" to Instant.now().plusSeconds(3600).toString()
            )

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<ShortenResponseDTO> = restTemplate.postForEntity(
                "$baseUrl/", entity, ShortenResponseDTO::class.java
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
            assertThat(response.body?.shortenedUrl).isNotNull()
        }

        @Test
        fun `should return 201 for valid deep in future expiryDate`() {
            val request = mapOf(
                "original_url" to "https://example.com",
                "expiry_date" to "2999-12-31T23:59:59Z"
            )

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<ShortenResponseDTO> = restTemplate.postForEntity(
                "$baseUrl/", entity, ShortenResponseDTO::class.java
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
            assertThat(response.body?.shortenedUrl).isNotNull()
        }

        @ParameterizedTest
        @ValueSource(
            strings =
                [
                    "not-a-date",
                    "2023-01-01",
                    "2023-01-01 12:00:00",
                    "01-01-2023T12:00:00Z"
                ]
        )
        fun `should return 400 for malformed expiryDate formats`(invalidDate: String) {
            val request = mapOf(
                "original_url" to "https://example.com",
                "expiry_date" to invalidDate
            )

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<ErrorResponseDTO> = restTemplate.postForEntity(
                "$baseUrl/", entity, ErrorResponseDTO::class.java
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(response.body?.error).contains("expiry_date: Invalid date format for expiryDate. Must be ISO-8601.")
        }

        @Test
        fun `should return 400 for empty expiryDate`() {
            val request = mapOf(
                "original_url" to "https://example.com",
                "expiry_date" to ""
            )

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<ErrorResponseDTO> = restTemplate.postForEntity(
                "$baseUrl/", entity, ErrorResponseDTO::class.java
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(response.body?.error).contains("expiry_date: Invalid date format for expiryDate. Must be ISO-8601.")
        }

        @Test
        fun `should return 201 if expiryDate is omitted`() {
            val request = mapOf(
                "original_url" to "https://example.com"
            )

            val entity = HttpEntity(request, headers)
            val response: ResponseEntity<ShortenResponseDTO> = restTemplate.postForEntity(
                "$baseUrl/", entity, ShortenResponseDTO::class.java
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
            assertThat(response.body?.shortenedUrl).isNotNull()
        }
    }

}