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

    implementation(libs.spring.kafka)

    developmentOnly(libs.spring.boot.devtools)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.bundles.testing.ecosystem)
}
