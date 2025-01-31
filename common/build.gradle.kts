plugins {
    kotlin("jvm") version "2.1.0"
}

group = "xelagurd.socialdating"
version = "0.8.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
}