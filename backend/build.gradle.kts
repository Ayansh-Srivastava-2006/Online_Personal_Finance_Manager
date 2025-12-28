plugins {
    id("java")
    id("application")
}

group = "com.example.finance.backend"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.eclipse.jetty:jetty-server:11.0.15")
    implementation("org.eclipse.jetty:jetty-servlet:11.0.15")
    implementation("com.google.code.gson:gson:2.10.1") // For JSON
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.mindrot:jbcrypt:0.4") // For password hashing
}

application {
    mainClass.set("com.example.finance.backend.ServerMain")
}
