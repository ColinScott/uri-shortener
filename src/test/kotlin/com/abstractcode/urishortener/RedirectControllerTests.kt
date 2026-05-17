package com.abstractcode.urishortener

import com.abstractcode.urishortener.uristore.UriStore
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.client.RestTestClient
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class RedirectControllerTests(@Autowired private val client: RestTestClient) {

    @MockkBean
    private lateinit var uriStore: UriStore

    @Test
    fun unknownKeyProduces404NotFound() {
        val unknownKey = RandomShortenerKeyGeneratorServiceImpl().generate()

        every { uriStore.getRedirectionUri(unknownKey) } returns null

        client.get().uri("/${unknownKey.key}").exchange().expectStatus().isNotFound
    }

    @Test
    fun knownKeyProduces301MovedPermanently() {
        val knownKey = RandomShortenerKeyGeneratorServiceImpl().generate()

        every { uriStore.getRedirectionUri(knownKey) } returns URI("https://example.com/mocked")

        client.get().uri("/${knownKey.key}").exchange().expectAll(
            { r -> r.expectStatus().isTemporaryRedirect },
            { r -> r.expectHeader().location("https://example.com/mocked") },
        )
    }
}
