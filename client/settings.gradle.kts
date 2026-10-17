import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

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
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.11"
    id("io.github.ben-manes.versions.settings") version "0.66.0"
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

gradle.rootProject {
    tasks.withType<DependencyUpdatesTask>().configureEach {
        revision = "release"
        rejectPreReleases = true
    }
}

include(":app")

rootProject.name = "client"