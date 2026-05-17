package com.abstractcode.urishortener

import com.abstractcode.urishortener.uristore.UriStore
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

data class AddUriRequest(val uri: URI)

val VALID_SCHEMES: Array<String> = arrayOf("http", "https")

/**
 * Controller for retrieving and managing shortened URIs.
 *
 * Assumes it will be at the base path.
 */
@RestController
@RequestMapping("/")
class UriShortenerController(val keyGenerator: ShortenerKeyGeneratorService, val uriStore: UriStore) {
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

            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(headers).body("Redirect")
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found")
        }
    }

    @PostMapping("/")
    suspend fun add(@RequestBody addRequest: AddUriRequest): ResponseEntity<Any> {
        if (!addRequest.uri.isAbsolute || !VALID_SCHEMES.contains(addRequest.uri.scheme)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("URI is invalid")
        }

        val key = keyGenerator.generate()

        uriStore.addUri(key, addRequest.uri)

        val redirectUri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .replacePath("/${key.key}")
            .build()
            .toUri()

        val headers = HttpHeaders()
        headers.location = redirectUri

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body("No Content")
    }
}
