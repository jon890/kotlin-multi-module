import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks {
    withType<BootJar> {
        enabled = false
    }

    withType<Jar> {
        enabled = true
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}