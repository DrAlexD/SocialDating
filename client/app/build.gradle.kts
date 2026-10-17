import java.util.Properties

val envProps = Properties().apply {
    rootProject.file(".env").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

fun prop(key: String) = envProps[key] as String?

val instrumentedCoverageReportTaskName = "createInstrumentedCoverageReport"

val isInstrumentedCoverageEnabled = gradle.startParameter.taskNames.any {
    it.substringAfterLast(":") == instrumentedCoverageReportTaskName
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.dagger.hilt.android)
    jacoco
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

android {
    namespace = "xelagurd.socialdating.client"
    compileSdk = 37

    defaultConfig {
        applicationId = "xelagurd.socialdating.client"
        minSdk = 36
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "xelagurd.socialdating.client.HiltTestRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = prop("KEYSTORE_PATH")?.let { file(it) }
            storePassword = prop("STORE_PASSWORD")
            keyAlias = prop("KEY_ALIAS")
            keyPassword = prop("KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/v1/\"")
            enableAndroidTestCoverage = isInstrumentedCoverageEnabled
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://socialdating.example.com/api/v1/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    androidResources {
        generateLocaleConfig = false
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        animationsDisabled = true
    }

    installation {
        // grants the runtime permissions declared in the manifest, so the debug build can reach the local server
        installOptions += "-g"
    }

    lint {
        baseline = file("lint-baseline.xml")
    }
}

tasks.register<JacocoReport>(instrumentedCoverageReportTaskName) {
    group = "verification"
    description = "Builds the instrumented test coverage report into tests-coverage/reports/instrumented."
    dependsOn("connectedDebugAndroidTest")

    val excludedClasses = listOf(
        "**/BuildConfig.class",
        "**/*Hilt_*.class",
        "**/*Factory*.class",
        "**/*_HiltModules*.class",
        "hilt_aggregated_deps/**",
        "dagger/hilt/**",
        "**/*_Impl*.class",
        "**/*ComposableSingletons*.class",
    )

    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
            exclude(excludedClasses)
        },
        fileTree(layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
            exclude(excludedClasses)
        },
    )
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("outputs/code_coverage/debugAndroidTest/connected/**/*.ec")
        }
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
        html.outputLocation.set(rootProject.layout.projectDirectory.dir("tests-coverage/reports/instrumented"))
        xml.outputLocation.set(rootProject.layout.projectDirectory.file("tests-coverage/reports/instrumented/report.xml"))
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core)

    // the coroutines come only transitively, so the BOM aligns their versions in the app and in the tests
    implementation(platform(libs.kotlinx.coroutines.bom))

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.androidx.compose)

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit)
    implementation(libs.okhttp)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.credentials)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // the Compose test libraries bring an older Espresso, which reflects on InputManager.getInstance removed in API 37
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.ui.tooling)
}