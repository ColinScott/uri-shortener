package com.abstractcode.urishortener.uristore

import com.abstractcode.urishortener.ShortenerKey
import org.springframework.stereotype.Repository
import java.net.URI

/**
 * [UriStore] implementation backed by an in-memory collection.
 *
 * Implemented to support development of other parts of the program, not intended to be usable in
 * production for hopefully obvious reasons.
 *
 * Uses [synchronized] rather than some form of ReadWriteLock as this is somewhat simpler and whether a
 * ReadWriteLock is actually more efficient is something that would need to be established with testing
 * that's not warranted here. A ReadWriteLock can be inefficient unless the operation overhead outweights
 * the synchronisation overhead, and these are not expensive operations.
 */
@Repository
class InMemoryUriStore : UriStore {
    val store = HashMap<ShortenerKey, URI>()

    override fun getRedirectionUri(key: ShortenerKey): URI? {
        return synchronized(store) {
            store[key]
        }
    }

    override fun addUri(
        key: ShortenerKey, uri: URI
    ): StoreResult {

        return synchronized(store) {
            if (store.containsKey(key)) {
                StoreResult.KeyAlreadyExists
            } else {
                store[key] = uri
                StoreResult.Success
            }
        }
    }
}
