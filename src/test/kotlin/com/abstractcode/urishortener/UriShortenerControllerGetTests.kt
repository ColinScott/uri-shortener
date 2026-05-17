package com.abstractcode.urishortener

import com.abstractcode.urishortener.uristore.UriStore
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(RandomShortenerKeyGeneratorServiceImpl::class)
@AutoConfigureRestTestClient
class UriShortenerControllerGetTests(@Autowired private val client: RestTestClient) {

    @MockkBean
    private lateinit var uriStore: UriStore

    @Test
    fun unknownKeyProduces404NotFound() {
        val unknownKey = RandomShortenerKeyGeneratorServiceImpl().generate()

        every { uriStore.getRedirectionUri(unknownKey) } returns null

        client.get().uri("/shortened/${unknownKey.key}").exchange().expectStatus().isNotFound
    }

    @Test
    fun knownKeyProducesGetResponse() {
        val knownKey = RandomShortenerKeyGeneratorServiceImpl().generate()

        every { uriStore.getRedirectionUri(knownKey) } returns URI("https://example.com/mocked")

        client.get().uri("/shortened/${knownKey.key}").exchange().expectAll(
            { r -> r.expectStatus().isOk },
            { r -> r.expectBody<GetUriResponse>().isEqualTo(GetUriResponse(URI("https://example.com/mocked"))) },
        )
    }
}
