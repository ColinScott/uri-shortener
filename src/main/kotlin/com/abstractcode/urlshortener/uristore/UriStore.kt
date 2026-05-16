package com.abstractcode.urlshortener.uristore

import com.abstractcode.urlshortener.ShortenerKey
import java.net.URI

enum class StoreResult {
    Success,
    KeyAlreadyExists
}

/**
 *
 */
interface UriStore {
    suspend fun getRedirectionUrl(key: ShortenerKey): URI?

    suspend fun addUrl(key: ShortenerKey, uri: URI): StoreResult
}