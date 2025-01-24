plugins {
    id("buildsrc.convention.kotlin-jvm")

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.kotlin.plugin.jpa)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.bundles.kotlinx.ecosystem)

    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)

    developmentOnly(libs.spring.boot.devtools)

    implementation(libs.spring.boot.webflux)
    implementation(libs.spring.cloud.gateway)

    implementation(libs.spring.kafka)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.mockk)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}