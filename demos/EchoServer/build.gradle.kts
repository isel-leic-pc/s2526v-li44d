plugins {
    kotlin("jvm") version "2.3.0"
    application
}

group = "palbp.demos.pc.isel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.16")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.16")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("palbp.demos.pc.isel.MainKt")
}

