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

