package com.abstractcode.urlshortener.uristore

import com.abstractcode.urlshortener.ShortenerKey
import java.net.URI

enum class StoreResult {
    Success, KeyAlreadyExists
}

/**
 * Store abstraction for persistence and retrieval of shortened URIs.
 */
interface UriStore {
    suspend fun getRedirectionUrl(key: ShortenerKey): URI?

    suspend fun addUrl(key: ShortenerKey, uri: URI): StoreResult
}