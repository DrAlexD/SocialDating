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

    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)

    developmentOnly(libs.spring.boot.devtools)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.h2database)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.mockk)
}
