plugins {
    id("buildsrc.convention.kotlin-jvm")

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.kotlin.plugin.jpa)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(project(":common"))

    implementation(libs.bundles.kotlinx.ecosystem)

    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)

    developmentOnly(libs.spring.boot.devtools)
    implementation(libs.bundles.spring.ecosystem)
    implementation(libs.spring.boot.security)

    runtimeOnly(libs.postgresql)

    implementation(libs.spring.kafka)

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    testImplementation(libs.h2database)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.mockk)
}
