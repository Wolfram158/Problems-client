plugins {
    kotlin("jvm") version "2.2.0"
    id("application")
    kotlin("plugin.serialization") version "2.2.0"
}

group = "ru.wolfram"
version = "1.0-SNAPSHOT"

application {
    mainClass = "ru.wolfram.problems_client.Application"
}

repositories {
    mavenCentral()
}

val ktorVersion = "3.2.3"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")

    testImplementation(kotlin("test"))
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}