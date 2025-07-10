package com.tohid.url_shortener.controller

import com.tohid.url_shortener.service.UrlService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/")
@RestController
class MainController(
    private val urlService: UrlService
) {

    @GetMapping("shorten")
    @Operation(summary = "Shorten a URL", description = "Returns a shortened version of the given original URL.")
    fun shorten(
        @RequestParam originalUrl: String
    ): ResponseEntity<ShortenResponse> {
        return ResponseEntity.ok(ShortenResponse(urlService.shorten(originalUrl)))
    }

    @GetMapping("{shortUrl}")
    @Operation(summary = "Resolve a short URL", description = "Returns the original URL for a given shortened version.")
    fun resolve(
        @PathVariable shortUrl: String,
    ): ResponseEntity<ResolveResponse> {
        val original = urlService.resolve(shortUrl)
        return if (original != null) {
            ResponseEntity.ok(ResolveResponse(original))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResolveResponse("Not Found"))
        }
    }

}

