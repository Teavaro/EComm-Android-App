buildscript {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath(BuildSystem.classPaths.kotlin)
        classpath(BuildSystem.classPaths.gradle)
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    id(BuildSystem.plugins.detektLint).version(BuildSystem.versions.detektLint)
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}

tasks.register("detektAll", io.gitlab.arturbosch.detekt.Detekt::class) {
    description = "Detekt build for all modules"
    parallel = true
    autoCorrect = false
    setSource(file(projectDir))
    config.setFrom(files("$rootDir/detekt/detekt.yml"))
    jvmTarget = "1.8"
    buildUponDefaultConfig = true
    // Suppress issues before adding detekt
    baseline.set(file("$rootDir/detekt/baseline.xml"))
    classpath.setFrom(project.configurations.getByName("detekt"))
    include("**/*.kt")
    exclude("**/build/**")
    // Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
    // ignoredVariants = listOf("productionRelease")
    reports {
        html {
            required.set(true)
            outputLocation.set(file("build/reports/detekt.html"))
        }
        xml.required.set(false)
        txt.required.set(false)
    }
}

val detektProjectBaseline by tasks.registering(io.gitlab.arturbosch.detekt.DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files(projectDir))
    config.setFrom(files("$rootDir/detekt/detekt.yml"))
    baseline.set(file("$rootDir/detekt/baseline.xml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    exclude("**/buildSrc/**")
    exclude("**/test/**/*.kt")
}