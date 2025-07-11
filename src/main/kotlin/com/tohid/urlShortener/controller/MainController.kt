package com.tohid.urlShortener.controller

import com.tohid.urlShortener.controller.dtos.ResolveResponseDTO
import com.tohid.urlShortener.controller.dtos.ShortenRequestDTO
import com.tohid.urlShortener.controller.dtos.ShortenResponseDTO
import com.tohid.urlShortener.service.UrlService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
class MainController(
    private val urlService: UrlService,
) {
    @PostMapping("/")
    @Operation(summary = "Shorten a URL", description = "Returns a shortened version of the given URL")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Short URL successfully created"),
        ApiResponse(responseCode = "400", description = "Invalid URL or bad request"),
        ApiResponse(responseCode = "500", description = "Unexpected error"),
    )
    fun shorten(
        @RequestBody @Valid shortenRequestDTO: ShortenRequestDTO,
    ): ResponseEntity<ShortenResponseDTO> {
        val shortenResponse = urlService.shorten(shortenRequestDTO)
        return ResponseEntity.created(URI.create(shortenResponse.shortenedUrl)).body(shortenResponse)
    }

    @GetMapping("/resolve/{shortUrl}")
    @Operation(summary = "Resolve a short URL", description = "Returns the original URL for a given shortened version")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Original URL resolved"),
        ApiResponse(responseCode = "404", description = "Short URL not found or expired"),
        ApiResponse(responseCode = "500", description = "Unexpected error"),
    )
    fun resolve(
        @PathVariable shortUrl: String,
    ): ResponseEntity<ResolveResponseDTO> {
        return ResponseEntity.ok(urlService.resolve(shortUrl))
    }

    @GetMapping("/{shortUrl}")
    @Operation(
        summary = "Redirect to the original URL",
        description =
            "Redirects to the original URL for a given short URL. " +
                "Returns 301 if the URL has no expiry date (permanent), or 302 if it does (temporary).",
    )
    @ApiResponses(
        ApiResponse(responseCode = "301", description = "Permanent redirect to the original URL"),
        ApiResponse(responseCode = "302", description = "Temporary redirect to the original URL (expiring link)"),
        ApiResponse(responseCode = "404", description = "Short URL not found or expired"),
        ApiResponse(responseCode = "500", description = "Unexpected server error"),
    )
    fun redirect(
        @PathVariable shortUrl: String,
    ) = urlService.findUrlByShortUrl(shortUrl)
}
