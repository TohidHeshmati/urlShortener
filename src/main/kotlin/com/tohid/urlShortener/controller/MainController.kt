package com.tohid.urlShortener.controller

import com.tohid.urlShortener.controller.dtos.ResolveResponseDTO
import com.tohid.urlShortener.controller.dtos.ShortenRequestDTO
import com.tohid.urlShortener.controller.dtos.ShortenResponseDTO
import com.tohid.urlShortener.service.UrlResolverService
import com.tohid.urlShortener.service.UrlService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RequestMapping("/")
@RestController
@Tag(name = "URL Shortener", description = "Operations related to shortening and resolving URLs")
class MainController(
    private val urlService: UrlService,
    private val urlResolverService: UrlResolverService,
) {
    @PostMapping("/api/v1/urls")
    @Operation(summary = "Shorten a URL", description = "Returns a shortened version of the given URL")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Short URL successfully created"),
        ApiResponse(responseCode = "400", description = "Invalid input"),
        ApiResponse(responseCode = "500", description = "Internal server error"),
    )
    fun shorten(
        @RequestBody @Valid shortenRequestDTO: ShortenRequestDTO,
    ): ResponseEntity<ShortenResponseDTO> {
        val shortenResponse = urlService.shorten(shortenRequestDTO)
        return ResponseEntity.created(URI.create(shortenResponse.shortenedUrl)).body(shortenResponse)
    }

    @GetMapping("api/v1/urls/{shortUrl}")
    @Operation(summary = "Resolve a short URL", description = "Returns the original URL for a given short URL")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "URL resolved successfully"),
        ApiResponse(responseCode = "404", description = "Short URL not found or expired"),
        ApiResponse(responseCode = "500", description = "Internal server error"),
    )
    fun resolve(
        @PathVariable shortUrl: String,
    ): ResponseEntity<ResolveResponseDTO> {
        return ResponseEntity.ok(urlResolverService.resolve(shortUrl))
    }

    @GetMapping("/{shortUrl}")
    @Operation(
        summary = "Redirect to the original URL",
        description = "Redirects to the original URL based on the expiration logic" + " (301 for permanent, 302 for temporary).",
    )
    @ApiResponses(
        ApiResponse(responseCode = "301", description = "Permanent redirect"),
        ApiResponse(responseCode = "302", description = "Temporary redirect"),
        ApiResponse(responseCode = "404", description = "Short URL not found or expired"),
        ApiResponse(responseCode = "500", description = "Internal server error"),
    )
    fun redirect(@PathVariable shortUrl: String) = urlService.redirectsByShortUrl(shortUrl)
}
