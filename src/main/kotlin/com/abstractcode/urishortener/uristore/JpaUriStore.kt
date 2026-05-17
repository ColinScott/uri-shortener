package com.abstractcode.urishortener.uristore

import com.abstractcode.urishortener.ShortenerKey
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.transaction.Transactional
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.net.URI
import kotlin.jvm.optionals.getOrNull

@Entity
class ShortenedUri(
    @Id
    var id: String? = null,
    var uri: URI
) {
    override fun toString(): String = "$id:$uri"
}

interface ShortenedUriRepository : CrudRepository<ShortenedUri, String> {}

@Repository
class JpaUriStore(val repository: ShortenedUriRepository) : UriStore {
    override fun getRedirectionUri(key: ShortenerKey): URI? {
        return repository.findById(key.key).map { shortenedUri -> shortenedUri.uri }.getOrNull()
    }

    @Transactional
    override fun addUri(
        key: ShortenerKey,
        uri: URI
    ): StoreResult {
        val shortenedUri = ShortenedUri(key.key, uri)

        return if (repository.existsById(key.key)) {
            StoreResult.KeyAlreadyExists
        } else {
            repository.save(shortenedUri)
            StoreResult.Success
        }
    }
}