dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.9"
}

kover {
    enableCoverage()

    reports {
        excludedClasses.add("*ServiceApplicationKt")
    }
}

gradle.rootProject {
    val testReportsDir = layout.projectDirectory.dir("tests-coverage/reports/test")
    tasks.matching { it.name == "koverHtmlReport" }.configureEach {
        (javaClass.getMethod("getHtmlDir").invoke(this) as DirectoryProperty)
            .set(testReportsDir)
    }
    tasks.matching { it.name == "koverXmlReport" }.configureEach {
        (javaClass.getMethod("getReportFile\$kover_gradle_plugin").invoke(this) as RegularFileProperty)
            .set(testReportsDir.file("report.xml"))
    }
}

include(":common")
include(":gateway-service")
include(":users-service")
include(":categories-service")
include(":defining-themes-service")
include(":statements-service")

rootProject.name = "server"