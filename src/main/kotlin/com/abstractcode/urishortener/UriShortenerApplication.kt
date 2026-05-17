package com.abstractcode.urishortener

import com.abstractcode.urishortener.uristore.InMemoryUriStore
import com.abstractcode.urishortener.uristore.JpaUriStore
import com.abstractcode.urishortener.uristore.ShortenedUriRepository
import com.abstractcode.urishortener.uristore.UriStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@SpringBootApplication
class UriShortenerApplication

fun main(args: Array<String>) {
    val a = runApplication<UriShortenerApplication>(*args)
}

@Configuration
@Profile("inmemory")
class InMemoryAppConfig {
    @Bean("uriStore")
    fun inMemoryUriStore(): UriStore {
        return InMemoryUriStore()
    }
}

@Configuration
@Profile("jpa")
class JpaAppConfig {
    @Bean("uriStore")
    fun jpaUriStore(@Autowired repository: ShortenedUriRepository): UriStore {
        return JpaUriStore(repository)
    }
}