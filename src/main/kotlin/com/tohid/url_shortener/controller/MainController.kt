package com.tohid.url_shortener.controller

import com.tohid.url_shortener.service.UrlService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/")
@RestController
class MainController(
    private val urlService: UrlService
) {

    @GetMapping("{shortUrl}")
    fun index(@PathVariable shortUrl: String): ResponseEntity<String> {
        val result = urlService.findUrlByShortUrl(shortUrl)
        return if (result != null) {
            ResponseEntity.ok(result.originalUrl)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short URL not found: $shortUrl")
        }
    }

    @GetMapping("all")
    fun getAll(): String {
        val allUrls = urlService.findAll()
        print(allUrls)
        return allUrls.toString()
    }

}

