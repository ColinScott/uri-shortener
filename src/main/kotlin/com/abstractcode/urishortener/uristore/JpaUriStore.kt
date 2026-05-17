package com.abstractcode.urishortener.uristore

import com.abstractcode.urishortener.ShortenerKey
import jakarta.persistence.Entity
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityManager
import jakarta.persistence.Id
import jakarta.persistence.PersistenceContext
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

interface ShortenedUriPersistRepository {
    fun persist(shortenedUri: ShortenedUri): StoreResult
}

class ShortenedUriPersistRepositoryImpl(
    @PersistenceContext
    private val entityManager: EntityManager
) : ShortenedUriPersistRepository {
    override fun persist(shortenedUri: ShortenedUri): StoreResult {
        try {
            entityManager.persist(shortenedUri)
            return StoreResult.Success
        } catch (_: EntityExistsException) {
            return StoreResult.KeyAlreadyExists
        }
    }
}

interface ShortenedUriRepository : CrudRepository<ShortenedUri, String>, ShortenedUriPersistRepository

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

        return repository.persist(shortenedUri)
    }
}