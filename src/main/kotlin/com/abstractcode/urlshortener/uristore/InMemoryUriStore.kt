package com.abstractcode.urlshortener.uristore

import com.abstractcode.urlshortener.ShortenerKey
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import java.net.URI

@Service
class InMemoryUriStore : UriStore {
    val mutex = Mutex()
    val store = HashMap<ShortenerKey, URI>()

    override suspend fun getRedirectionUrl(key: ShortenerKey): URI? {
        return mutex.withLock {
            store[key]
        }
    }

    override suspend fun addUrl(
        key: ShortenerKey,
        uri: URI
    ): StoreResult {
        return mutex.withLock {
            if (store.containsKey(key)) {
                StoreResult.KeyAlreadyExists
            } else {
                store[key] = uri
                StoreResult.Success
            }
        }
    }

}
