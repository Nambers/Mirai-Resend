plugins {
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "tech.eritquearcus"
version = "1.0"

dependencies {
    implementation("com.google.code.gson:gson:2.8.9")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public")
}
