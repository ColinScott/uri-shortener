package com.abstractcode.urishortener

import com.abstractcode.urishortener.uristore.InMemoryUriStore
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(InMemoryUriStore::class, RandomShortenerKeyGeneratorServiceImpl::class)
@AutoConfigureRestTestClient
class UriShortenerControllerAddUriTests(@Autowired private val client: RestTestClient) {
    @Test
    fun canStoreAWellFormattedUri() {
        val shortenedUri =
            client.post()
                .uri("/shortened")
                .body(AddUriRequest(URI("https://example.com/add")))
                .exchange()
                .expectAll(
                    { r -> r.expectStatus().isCreated }
                )
                .returnResult()
                .responseHeaders
                .location!!

        client.get()
            .uri(shortenedUri)
            .exchange()
            .expectAll(
                { r -> r.expectStatus().isTemporaryRedirect },
                { r -> r.expectHeader().location("https://example.com/add") }
            )
    }

    @Test
    fun missingBodyProduces400BadRequest() {
        client.post()
            .uri("/shortened")
            .exchange()
            .expectAll(
                { r -> r.expectStatus().isBadRequest }
            )
            .returnResult()
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "{}", "{ bad json", "{ \"uri\": \"not a uri\" }"
    ])
    fun invalidBodiesProduce400BadRequest(body: String) {
        client.post()
            .uri("/shortened")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .exchange()
            .expectAll(
                { r -> r.expectStatus().isBadRequest }
            )
            .returnResult()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "{ \"uri\": 11111 }",
            "{ \"uri\": \"example.com/11111\" }",
            "{ \"uri\": \"ftp://example.com/11111\" }",
            "{ \"uri\": \"file:///example.com/11111\" }"
        ]
    )
    fun invalidUrisProduce422UnprocessableEntity(body: String) {
        client.post()
            .uri("/shortened")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .exchange()
            .expectAll(
                { r -> r.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT) }
            )
            .returnResult()
    }
}