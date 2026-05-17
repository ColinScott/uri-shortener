package com.abstractcode.urishortener

import com.abstractcode.urishortener.uristore.StoreResult
import com.abstractcode.urishortener.uristore.UriStore
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.client.RestTestClient
import java.net.URI


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(RandomShortenerKeyGeneratorServiceImpl::class)
@AutoConfigureRestTestClient
class UriShortenerControllerDuplicateKeyTests(@Autowired private val client: RestTestClient) {
    @MockkBean
    private lateinit var uriStore: UriStore

    @Test
    fun keyAlreadyExistsProduces500InternalServerError() {

        every { uriStore.addUri(any(), any()) } returns StoreResult.KeyAlreadyExists

        client.post().uri("/")
            .body(AddUriRequest(URI("https://example.com/duplicatekey")))
            .exchange()
            .expectAll(
                { r -> r.expectStatus().is5xxServerError }
            )
            .returnResult()
    }
}