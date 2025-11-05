plugins {
    id("buildsrc.convention.kotlin-jvm")

    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    api(libs.bundles.kotlin.ecosystem)
    api(libs.bundles.spring.ecosystem)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.test)
}