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
include(":users-service")
include(":categories-service")
include(":defining-themes-service")
include(":statements-service")

rootProject.name = "SocialDating"