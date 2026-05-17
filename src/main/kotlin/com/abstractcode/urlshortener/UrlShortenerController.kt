package com.abstractcode.urlshortener

import com.abstractcode.urlshortener.uristore.UriStore
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/")
class UrlShortenerController(val uriStore: UriStore) {
    @GetMapping("/{key}")
    suspend fun redirect(@PathVariable key: ShortenerKey): ResponseEntity<Any> {
        val uri = uriStore.getRedirectionUrl(key)

        if (uri != null) {
            val headers = HttpHeaders()
            headers.location = uri

            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(headers).body("Redirect")
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found")
        }
    }
}
