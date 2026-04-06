import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.koin.compiler)
    id("jacoco")
}

android {
    namespace = "com.praktikum.playlistmaker"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.praktikum.playlistmaker"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    testOptions {
        unitTests.all {
            it.testLogging {
                events =
                    setOf(
                        TestLogEvent.PASSED,
                        TestLogEvent.FAILED,
                        TestLogEvent.SKIPPED,
                    )
                showStandardStreams = true
                exceptionFormat = TestExceptionFormat.FULL
            }
        }
    }
}

val coverageExcludes =
    listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
    )

val debugClassDirectories =
    files(
        fileTree("${layout.buildDirectory.asFile.get()}/tmp/kotlin-classes/debug") {
            exclude(coverageExcludes)
        },
        fileTree("${layout.buildDirectory.asFile.get()}/intermediates/javac/debug/classes") {
            exclude(coverageExcludes)
        },
    )

val debugSourceDirectories =
    files(
        "src/main/java",
        "src/main/kotlin",
    )

tasks.register<JacocoReport>("jacocoDebugUnitTestReport") {
    group = "verification"
    description = "Generates JaCoCo XML report for debug unit tests"
    dependsOn("testDebugUnitTest")

    classDirectories.setFrom(debugClassDirectories)
    sourceDirectories.setFrom(debugSourceDirectories)
    executionData.setFrom(
        fileTree(layout.buildDirectory.asFile.get()) {
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
            include("jacoco/testDebugUnitTest.exec")
        },
    )

    reports {
        xml.required.set(true)
        html.required.set(false)
        csv.required.set(false)
        xml.outputLocation.set(
            layout.buildDirectory.file(
                "reports/jacoco/jacocoDebugUnitTestReport/jacocoDebugUnitTestReport.xml",
            ),
        )
    }
}

tasks.register<JacocoReport>("jacocoDebugAndroidTestReport") {
    group = "verification"
    description = "Generates JaCoCo XML report for debug instrumented tests"
    dependsOn("connectedDebugAndroidTest")

    classDirectories.setFrom(debugClassDirectories)
    sourceDirectories.setFrom(debugSourceDirectories)
    executionData.setFrom(
        fileTree(layout.buildDirectory.asFile.get()) {
            include("outputs/code_coverage/debugAndroidTest/connected/**/*.ec")
        },
    )

    reports {
        xml.required.set(true)
        html.required.set(false)
        csv.required.set(false)
        xml.outputLocation.set(
            layout.buildDirectory.file(
                "reports/jacoco/jacocoDebugAndroidTestReport/jacocoDebugAndroidTestReport.xml",
            ),
        )
    }
}

tasks.register("jacocoAllXmlReports") {
    group = "verification"
    description = "Generates JaCoCo XML reports for unit and instrumented tests"
    dependsOn("jacocoDebugUnitTestReport", "jacocoDebugAndroidTestReport")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.glide)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.koin.test)
}

ktlint {
    android = true
    ignoreFailures = false
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
}

tasks.register<GradleBuild>("androidTestVerbose") {
    group = "verification"
    description = "Runs connectedDebugAndroidTest with info-level logging"
    tasks = listOf("connectedDebugAndroidTest")
    startParameter.logLevel = LogLevel.INFO
}
