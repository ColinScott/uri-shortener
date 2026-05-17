package com.abstractcode.urlshortener.uristore

import com.abstractcode.urlshortener.ShortenerKey
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Repository
import java.net.URI

/**
 * [UriStore] implementation backed by an in-memory collection.
 *
 * Implemented to support development of other parts of the program, not intended to be usable in
 * production for hopefully obvious reasons.
 *
 * Uses [Mutex] rather than some form of ReadWriteLock as this is somewhat simpler and whether a
 * ReadWriteLock is actually more efficient is something that would need to be established with testing
 * that's not warranted here. A ReadWriteLock can be inefficient unless the operation overhead outweights
 * the synchronisation overhead, and these are not expensive operations.
 */
@Repository
class InMemoryUriStore : UriStore {
    val mutex = Mutex()
    val store = HashMap<ShortenerKey, URI>()

    override suspend fun getRedirectionUrl(key: ShortenerKey): URI? {
        return mutex.withLock {
            store[key]
        }
    }

    override suspend fun addUrl(
        key: ShortenerKey, uri: URI
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
