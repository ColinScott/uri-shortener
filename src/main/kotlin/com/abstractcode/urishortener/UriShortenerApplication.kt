package com.abstractcode.urishortener

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UriShortenerApplication

fun main(args: Array<String>) {
    runApplication<UriShortenerApplication>(*args)
}
