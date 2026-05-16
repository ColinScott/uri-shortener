package com.abstractcode.urlshortener

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.assertNotSame

class RandomShortenerKeyGeneratorServiceImplTests {
    @Test
    fun shouldGenerateCorrectlyStructuredKey() {
        val key = RandomShortenerKeyGeneratorServiceImpl().generate()

        assertAll(
            { assertEquals("Key not expected length", KEY_LENGTH, key.key.length) },
            {
                assertTrue(
                    key.key.toCharArray().all { c -> c.isLetterOrDigit() },
                    "All key characters must be letters or digits"
                )
            },
            {
                // Avoiding repeating the code under test using `isLetterOrDigit` would by itself permit many characters
                // outside of the desired range.
                assertTrue(
                    key.key.toCharArray().all { c -> c in '0'..'z' },
                    "All key characters must be in expected range"
                )
            },
        )
    }

    @Test
    fun generatedKeysShouldBeDistinct() {
        val generator = RandomShortenerKeyGeneratorServiceImpl()

        val firstKey = generator.generate()
        val secondKey = generator.generate()

        assertNotEquals( firstKey, secondKey, "Generated keys should not be the same")
    }
}