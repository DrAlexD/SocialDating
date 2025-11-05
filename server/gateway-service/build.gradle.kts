plugins {
    id("buildsrc.convention.kotlin-jvm")

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.kotlin.plugin.jpa)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.bundles.kotlin.ecosystem)

    implementation(libs.spring.boot.actuator)

    implementation(libs.spring.boot.webflux)
    implementation(libs.spring.cloud.gateway)
    implementation(platform(libs.spring.cloud.dependencies))

    implementation(libs.spring.kafka)

    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)

    developmentOnly(libs.spring.boot.devtools)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.mockk)
}