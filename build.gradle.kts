// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

tasks.register<Copy>("installGitHooks") {
    from(file(".githooks"))
    into(file(".git/hooks"))
    filePermissions {
        unix("rwxr-xr-x")
    }
}