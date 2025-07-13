plugins {
    // Kotlin
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.jpa") version "2.0.21"

    // Spring
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"

    // Tools
    id("org.flywaydb.flyway") version "9.22.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
}

group = "com.tohid.url_shortener"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}
// Kotlin
val kotlinVersion = "1.9.25"

// Spring
val springVersion = "3.5.3"
val springDocVersion = "2.8.9"

// Jackson
val jacksonVersion = "2.15.3"

// JPA & DB
val flywayVersion = "11.10.1"
val mysqlConnectorVersion = "8.0.33"

// Testing
val junitLauncherVersion = "1.10.2"
val mockitoKotlinVersion = "5.1.0"
val apacheClientVersion = "5.5"

// Validation
val hibernateValidatorVersion = "8.0.2.Final"
val jakartaValidationVersion = "3.1.1"


dependencies {
    // --- Core ---
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    // --- Spring Data & Persistence ---
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:$springVersion")
    implementation("mysql:mysql-connector-java:$mysqlConnectorVersion")

    // --- Migrations ---
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    runtimeOnly("org.flywaydb:flyway-mysql:$flywayVersion")

    // --- Validation ---
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")

    // --- Documentation & Monitoring ---
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springVersion")

    // --- Testing ---
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")
    testImplementation("org.apache.httpcomponents.client5:httpclient5:$apacheClientVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitLauncherVersion")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = 1
}
