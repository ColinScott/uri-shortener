package com.abstractcode.urlshortener.uristore

import com.abstractcode.urlshortener.RandomShortenerKeyGeneratorServiceImpl
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

        assertNull(store.getRedirectionUrl(keyGenerator.generate()), "Empty Store should not return URI")
    }

    @Test
    fun getNonMatchingKeyFromStoreReturnsNull() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()

        store.addUrl(keyGenerator.generate(), URI("https://example.com/"))

        assertNull(
            store.getRedirectionUrl(keyGenerator.generate()),
            "Single item Store should not return item for non-matching key"
        )
    }

    @Test
    fun getItemFromStoreWithSingleItemReturnsExpectedUri() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()
        val singleKey = keyGenerator.generate()

        store.addUrl(singleKey, URI("https://example.com/"))

        assertEquals(
            URI("https://example.com/"),
            store.getRedirectionUrl(singleKey),
            "Single item Store should return expected URI for matching key"
        )
    }

    @Test
    fun getItemFromStoreWithMultipleItemsReturnsExpectedUri() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()
        val expectedKey = keyGenerator.generate()

        store.addUrl(expectedKey, URI("https://example.com/expected"))

        for (i in 1..5) {
            store.addUrl(keyGenerator.generate(), URI("https://example.com/$i"))
        }

        assertEquals(
            URI("https://example.com/expected"),
            store.getRedirectionUrl(expectedKey),
            "Multiple item Store should return expected URI for matching key"
        )
    }

    @Test
    fun addOfUniqueKeyReturnsSuccess() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()

        assertEquals(
            StoreResult.Success,
            store.addUrl(keyGenerator.generate(), URI("https://example.com/")),
            "Successful add should return Success result"
        )
    }

    @Test
    fun addOfDuplicateKeyDoesNotChangeStore() = runTest {
        val store = InMemoryUriStore()

        val keyGenerator = RandomShortenerKeyGeneratorServiceImpl()

        val duplicateKey = keyGenerator.generate()

        store.addUrl(duplicateKey, URI("https://example.com/original"))

        val secondAddResult = store.addUrl(duplicateKey, URI("https://example.com/update"))

        val uriAfterSecondAdd = store.getRedirectionUrl(duplicateKey)
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
