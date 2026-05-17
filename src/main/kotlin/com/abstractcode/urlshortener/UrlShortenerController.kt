package com.abstractcode.urlshortener

import com.abstractcode.urlshortener.uristore.UriStore
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for retrieving and managing shortened URIs.
 *
 * Assumes it will be at the base path.
 */
@RestController
@RequestMapping("/")
class UrlShortenerController(val uriStore: UriStore) {
    /**
     * Perform a redirect to a URI when given a [key][ShortenerKey].
     *
     * Uses 307 TEMPORARY REDIRECT as the URI may change over time.
     *
     * Returns 404 NOT FOUND if the key is unknown.
     */
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
