plugins {
    java
    application
}

group = "com.example.finance"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("com.example.finance.backend.ServerMain")
}

repositories {
    mavenCentral()
}

dependencies {
    // Jetty Server (version 11 uses Jakarta Servlet API 5.0)
    implementation("org.eclipse.jetty:jetty-server:11.0.20")
    implementation("org.eclipse.jetty:jetty-servlet:11.0.20")

    // Jakarta Servlet API (needed for compilation, Jetty provides it at runtime)
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")

    // MySQL Connector
    implementation("mysql:mysql-connector-java:8.0.33")

    // Gson for JSON processing
    implementation("com.google.code.gson:gson:2.13.2")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}