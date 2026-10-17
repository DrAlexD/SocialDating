import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("buildsrc.convention.kotlin-jvm")

    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.plugin.spring)
}

// the module is not a Spring Boot application, so the Boot BOM is imported the same way as the Boot plugin does it
// in services, including the Kotlin version taken from the Kotlin plugin instead of the one from the BOM
dependencyManagement {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES) {
            bomProperty("kotlin.version", libs.versions.kotlin.get())
        }
    }
}

dependencies {
    api(libs.bundles.kotlin.ecosystem)
    api(libs.bundles.spring.ecosystem)

    implementation(libs.mockk)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.webmvc.test)
    testImplementation(libs.spring.boot.restclient)
}