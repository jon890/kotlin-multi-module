import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks {
    withType<BootJar> {
        enabled = true
        archiveFileName.set("${project.name}.jar")
        archiveClassifier = ""
    }

    withType<Jar> {
        enabled = true
        archiveClassifier = "plain"
    }
}

dependencies {
    // Spring Boot
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)

    // OpenTelemetry BOM for version alignment
    implementation(platform(libs.opentelemetry.bom))
    implementation(platform(libs.opentelemetry.instrumentation.bom))
    
    // OpenTelemetry API (Java Agent가 자동 계측을 담당)
    implementation(libs.opentelemetry.api)
    implementation(libs.opentelemetry.annotations)
    
    // Structured logging
    implementation(libs.jackson.databind)
    implementation(libs.logstash.logback.encoder)
}