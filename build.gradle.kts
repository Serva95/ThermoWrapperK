import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "4.1.2"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "2.4.10"
	kotlin("plugin.spring") version "2.4.10"
	kotlin("plugin.jpa") version "2.4.10"
}

group = "it.srv"
version = "1.3.0-4.1.2_3.6.0_42.7.4"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

var ktorVersion = "3.6.0"
var postgresVersion = "42.7.4"

dependencies {
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.ktor:ktor-client:$ktorVersion")
	implementation("io.ktor:ktor-client-cio:$ktorVersion")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql:$postgresVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
	testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
		jvmTarget.set(JvmTarget.JVM_17)
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
