import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "2.0.21"
    `maven-publish`
    id("com.google.protobuf") version "0.9.4"
}

group = "com.github.leroyramaphoko"
version = "1.0.20"

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.protobuf:protobuf-kotlin:3.25.3")
    api("io.grpc:grpc-kotlin-stub:1.4.1")
    api("io.grpc:grpc-protobuf:1.62.2")
    api("javax.annotation:javax.annotation-api:1.3.2")
}

kotlin {
    jvmToolchain(21)
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/java")
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/grpckt")
            srcDirs("build/generated/source/proto/main/kotlin")
        }
    }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

java {
    withSourcesJar()
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.25.3" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.62.2" }
        id("grpckt") { artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar" }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
                id("grpckt")
            }
            task.builtins {
                id("kotlin")
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "retail-admin-protos"
        }
    }
}