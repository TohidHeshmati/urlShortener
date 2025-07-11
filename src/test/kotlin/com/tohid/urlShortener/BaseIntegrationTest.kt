package com.tohid.urlShortener

import com.fasterxml.jackson.databind.ObjectMapper
import com.tohid.urlShortener.repository.UrlRepository
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationTest() {

    @Autowired
    protected lateinit var urlRepository: UrlRepository

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @LocalServerPort
    private var port: Int = 0

    protected lateinit var baseUrl: String


    companion object {
        @Container
        val mysql: MySQLContainer<*> = MySQLContainer("mysql:8.0").apply {
            withDatabaseName("url_shortener_test")
            withUsername("test")
            withPassword("test")
            withReuse(true)
            withExposedPorts(3306)
            portBindings = listOf("63490:3306")
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { mysql.jdbcUrl }
            registry.add("spring.datasource.username") { mysql.username }
            registry.add("spring.datasource.password") { mysql.password }
            println("MySQL Testcontainer info:")
            println("JDBC URL: ${mysql.jdbcUrl}")
            println("Username: ${mysql.username}")
            println("Password: ${mysql.password}")
            println("Host: ${mysql.host}")
            println("Port: ${mysql.getMappedPort(3306)}")
        }
    }

    @BeforeEach
    fun cleanup() {
        baseUrl = "http://localhost:$port"
        println("Base URL for tests: $baseUrl")
        urlRepository.deleteAll()
    }

    val headers: HttpHeaders =
        HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

    val redirectSafeRestTemplate =
        RestTemplate(
            HttpComponentsClientHttpRequestFactory().apply {
                httpClient =
                    HttpClients.custom()
                        .disableRedirectHandling()
                        .build()
            },
        )
}
