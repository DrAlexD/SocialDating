dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

include(":gateway-service")
include(":categories-service")
include(":defining-themes-service")

rootProject.name = "SocialDating"