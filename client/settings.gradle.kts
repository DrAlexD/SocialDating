pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.8"
}

kover {
    enableCoverage()

    reports {
        excludedClasses.addAll(
            "*.BuildConfig",
            "*Hilt_*",
            "*Factory*",
            "*_HiltModules*",
            "hilt_aggregated_deps.*",
            "dagger.hilt.*",
            "*_Impl*",
            "*ComposableSingletons*",
        )
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

include(":app")

rootProject.name = "client"