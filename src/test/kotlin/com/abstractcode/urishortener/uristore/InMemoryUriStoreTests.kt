package com.abstractcode.urishortener.uristore

import com.abstractcode.urishortener.RandomShortenerKeyGeneratorServiceImpl
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import java.net.URI
import kotlin.test.assertEquals

class InMemoryUriStoreTests {
    @Test
    fun gettingUnknownKeyFromEmptyStoreReturnsNull() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()

        assertNull(store.getRedirectionUri(keyGenerator.generate()), "Empty Store should not return URI")
    }

    @Test
    fun getNonMatchingKeyFromStoreReturnsNull() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()

        store.addUri(keyGenerator.generate(), URI("https://example.com/"))

        assertNull(
            store.getRedirectionUri(keyGenerator.generate()),
            "Single item Store should not return item for non-matching key"
        )
    }

    @Test
    fun getItemFromStoreWithSingleItemReturnsExpectedUri() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()
        val singleKey = keyGenerator.generate()

        store.addUri(singleKey, URI("https://example.com/"))

        assertEquals(
            URI("https://example.com/"),
            store.getRedirectionUri(singleKey),
            "Single item Store should return expected URI for matching key"
        )
    }

    @Test
    fun getItemFromStoreWithMultipleItemsReturnsExpectedUri() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()
        val expectedKey = keyGenerator.generate()

        store.addUri(expectedKey, URI("https://example.com/expected"))

        for (i in 1..5) {
            store.addUri(keyGenerator.generate(), URI("https://example.com/$i"))
        }

        assertEquals(
            URI("https://example.com/expected"),
            store.getRedirectionUri(expectedKey),
            "Multiple item Store should return expected URI for matching key"
        )
    }

    @Test
    fun addOfUniqueKeyReturnsSuccess() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()

        assertEquals(
            StoreResult.Success,
            store.addUri(keyGenerator.generate(), URI("https://example.com/")),
            "Successful add should return Success result"
        )
    }

    @Test
    fun addOfDuplicateKeyDoesNotChangeStore() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()

        val duplicateKey = keyGenerator.generate()

        store.addUri(duplicateKey, URI("https://example.com/original"))

        val secondAddResult = store.addUri(duplicateKey, URI("https://example.com/update"))

        val uriAfterSecondAdd = store.getRedirectionUri(duplicateKey)
        assertAll({
            assertEquals(
                StoreResult.KeyAlreadyExists, secondAddResult, "Duplicate add results in KeyAlreadyExists result"
            )
        }, {
            assertEquals(
                URI("https://example.com/original"), uriAfterSecondAdd, "Duplicate add does not change "
            )
        })
    }
}
