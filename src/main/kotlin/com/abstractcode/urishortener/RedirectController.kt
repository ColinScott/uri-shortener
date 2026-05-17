package com.abstractcode.urishortener

import com.abstractcode.urishortener.uristore.UriStore
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller for the redirect of shortened URIs.
 */
@RestController
class RedirectController(val uriStore: UriStore) {
    val logger: Log = LogFactory.getLog(this::class.java)

    /**
     * Perform a redirect to a URI when given a [key][ShortenerKey].
     *
     * Uses 307 TEMPORARY REDIRECT as the URI may change over time.
     *
     * Returns 404 NOT FOUND if the key is unknown.
     */
    @GetMapping("/{key}")
    suspend fun redirect(@PathVariable key: ShortenerKey): ResponseEntity<Any> {
        val uri = uriStore.getRedirectionUri(key)

        if (uri != null) {
            val headers = HttpHeaders()
            headers.location = uri

            logger.info("Redirecting key '${key.key}' to '$uri'")

            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(headers).body("Redirect")
        } else {
            // As key is unvalidated user input at this point do not log it
            logger.warn("Failed to redirect to unknown key")

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found")
        }
    }
}
