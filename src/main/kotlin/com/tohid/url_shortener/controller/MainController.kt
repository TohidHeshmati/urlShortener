package com.tohid.url_shortener.controller

import com.tohid.url_shortener.service.UrlService
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
    fun shorten(
        @RequestParam originalUrl: String
    ): ResponseEntity<ShortenResponse> {
        return ResponseEntity.ok(ShortenResponse(urlService.shorten(originalUrl)))
    }

    @GetMapping("{shortUrl}")
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

