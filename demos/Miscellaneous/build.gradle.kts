import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.3.21"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
}

group = "palbp.demos.pc.isel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}
