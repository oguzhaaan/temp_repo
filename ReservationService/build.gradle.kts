plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("kapt") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
}

group = "g11"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

jacoco {
	toolVersion = "0.8.10"
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-common:2.7.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("org.postgresql:postgresql")
	implementation("jakarta.validation:jakarta.validation-api:3.1.1")
	implementation ("org.hibernate.validator:hibernate-validator:7.0.5.Final")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	kapt("org.springframework.boot:spring-boot-configuration-processor")
	// Kafka dependencies
	implementation("org.springframework.kafka:spring-kafka")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
	testImplementation("org.mockito:mockito-core:5.10.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1") // for Kotlin-friendly mocking
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

kapt {
	correctErrorTypes = true
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests must run before report is generated

	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
	}
}
