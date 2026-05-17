plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    id("org.springframework.boot").version(libs.versions.springBootVersion)
    id("io.spring.dependency-management").version(libs.versions.springDependencyManagementVersion)
}

group = "com.abstractcode"
version = "0.0.1-SNAPSHOT"
description = "url-shortener"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.spring.boot.starter.webmvc)

    developmentOnly(libs.spring.boot.devtools)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.springmockk)

    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
