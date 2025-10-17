plugins {
    kotlin("jvm") version "2.2.0"
    id("application")
    kotlin("plugin.serialization") version "2.2.0"
}

group = "ru.wolfram"
version = "1.0-SNAPSHOT"

application {
    mainClass = "ru.wolfram.problems_client.presentation.Application"
}

repositories {
    mavenCentral()
}

val ktorVersion = "3.2.3"
val mordantVersion = "3.0.2"

dependencies {
    implementation("com.github.ajalt.mordant:mordant:$mordantVersion")
    implementation("com.github.ajalt.mordant:mordant-markdown:$mordantVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp-jvm:3.2.3")

    testImplementation(kotlin("test"))
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest.attributes["Main-Class"] = application.mainClass
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

kotlin {
    jvmToolchain(21)
}