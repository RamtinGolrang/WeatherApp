plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.weatherapp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin-standardbibliotek
    implementation(kotlin("stdlib"))

    // Javalin - webramverk
    implementation("io.javalin:javalin:5.6.1")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    // Jackson - JSON-hantering
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    // OkHttp - HTTP-klient
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // För testning
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
}

application {
    mainClass.set("com.weatherapp.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
}