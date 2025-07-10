package com.tohid.url_shortener

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/")
class HomeController {
    @GetMapping ("home")
    fun home(): String {
        return "Welcome to the URL Shortener Service!"
    }
}