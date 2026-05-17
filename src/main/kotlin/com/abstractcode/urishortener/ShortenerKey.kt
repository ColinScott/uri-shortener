package com.abstractcode.urishortener

import org.springframework.stereotype.Service

/**
 * A key to a shortened URI.
 */
@JvmInline
value class ShortenerKey(val key: String)

/**
 * Generate a [ShortenerKey].
 *
 * Permits the generation strategy to be replaced.
 */
interface ShortenerKeyGeneratorService {
    fun generate(): ShortenerKey
}

const val KEY_LENGTH = 8

/**
 * Generate a random [ShortenerKey] with length [KEY_LENGTH].
 */
@Service
class RandomShortenerKeyGeneratorServiceImpl : ShortenerKeyGeneratorService {
    companion object {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    }

    override fun generate(): ShortenerKey {
        val key = (1..KEY_LENGTH)
            .map { allowedChars.random() }
            .joinToString("")

        return ShortenerKey(key)
    }
}