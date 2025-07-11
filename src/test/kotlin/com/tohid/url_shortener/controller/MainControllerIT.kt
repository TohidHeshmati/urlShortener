package com.tohid.url_shortener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tohid.url_shortener.controller.dtos.ErrorResponseDTO
import com.tohid.url_shortener.controller.dtos.ResolveResponseDTO
import com.tohid.url_shortener.controller.dtos.ShortenRequestDTO
import com.tohid.url_shortener.controller.dtos.ShortenResponseDTO
import com.tohid.url_shortener.domain.Url
import com.tohid.url_shortener.repository.UrlRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
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

    private val baseUrl = "http://localhost:$port"

    private val headers: HttpHeaders = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
    }


    @BeforeEach
    fun cleanDB() {
        urlRepository.deleteAll()
    }

    @Test
    fun `should shorten a valid URL`() {
        val entity = HttpEntity(
            ShortenRequest(originalUrl = "https://www.example.com"), HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            })

        val response: ResponseEntity<ShortenResponse> = restTemplate.postForEntity(
            "$baseUrl/", entity, ShortenResponse::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        assertThat(response.body!!.shortenedUrl).isNotBlank
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
    }

}