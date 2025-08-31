import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)

    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false

    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
}

allprojects {
    group = "com.bifos"
    version = "1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.kapt")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.jetbrains.kotlin.plugin.jpa")
        plugin("org.jetbrains.kotlin.plugin.noarg")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    // JVM Toolchain을 사용하여 모든 작업이 동일한 Java 버전 사용
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks {
        withType<KotlinCompile> {
            compilerOptions {
                freeCompilerArgs.addAll("-Xjsr305=strict")
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }

        withType<BootJar> {
            enabled = false
        }

        withType<Jar> {
            enabled = true
        }
    }
}