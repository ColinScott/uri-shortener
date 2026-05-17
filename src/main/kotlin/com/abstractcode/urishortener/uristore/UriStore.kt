package com.abstractcode.urishortener.uristore

import com.abstractcode.urishortener.ShortenerKey
import java.net.URI

enum class StoreResult {
    Success, KeyAlreadyExists
}

/**
 * Store abstraction for persistence and retrieval of shortened URIs.
 */
interface UriStore {
    fun getRedirectionUri(key: ShortenerKey): URI?

    fun addUri(key: ShortenerKey, uri: URI): StoreResult
}