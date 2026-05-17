package com.abstractcode.urishortener

import com.abstractcode.urishortener.uristore.StoreResult
import com.abstractcode.urishortener.uristore.UriStore
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

data class GetUriResponse(val uri: URI)
data class AddUriRequest(val uri: URI)

val VALID_SCHEMES: Array<String> = arrayOf("http", "https")

/**
 * Controller for retrieving and managing shortened URIs.
 */
@RestController
class UriShortenerController(val keyGenerator: ShortenerKeyGeneratorService, val uriStore: UriStore) {
    /**
     * Get the details of a shortened URI by [key][ShortenerKey]. This will initially contain only the full URI.
     *
     * Returns 404 NOT FOUND if the key is unknown.
     */
    @GetMapping("/shortened/{key}")
    suspend fun get(@PathVariable key: ShortenerKey): ResponseEntity<Any> {
        val uri = uriStore.getRedirectionUri(key)

        return if (uri != null) {
            ResponseEntity.ok(GetUriResponse(uri))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found")
        }
    }

    @PostMapping("/shortened")
    suspend fun add(@RequestBody addRequest: AddUriRequest): ResponseEntity<Any> {
        if (!addRequest.uri.isAbsolute || !VALID_SCHEMES.contains(addRequest.uri.scheme)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("URI is invalid")
        }

        val key = keyGenerator.generate()

        if (uriStore.addUri(key, addRequest.uri) == StoreResult.KeyAlreadyExists) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Duplicate key detected")
        }

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
