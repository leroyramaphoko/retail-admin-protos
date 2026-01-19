import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "2.0.21"
    `maven-publish`
    id("com.google.protobuf") version "0.9.4"
}

group = "com.github.leroyramaphoko"
version = "1.0.23" // incremented version

repositories {
    mavenCentral()
}

dependencies {
    // Core protobuf + gRPC
    api("com.google.protobuf:protobuf-kotlin:3.25.3")
    api("io.grpc:grpc-kotlin-stub:1.4.1")
    api("io.grpc:grpc-protobuf:1.62.2")

    // Annotation API (needed for generated code)
    api("javax.annotation:javax.annotation-api:1.3.2")
}

kotlin {
    jvmToolchain(21)
}

sourceSets {
    main {
        // This is the crucial part: tell the Kotlin compiler where the generated code is
        kotlin.srcDirs(
            "build/generated/source/proto/main/grpckt",
            "build/generated/source/proto/main/kotlin"
        )
        java.srcDirs(
            "build/generated/source/proto/main/java",
            "build/generated/source/proto/main/grpc"
        )
    }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

java {
    withSourcesJar()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.62.2"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            // Generate Kotlin, gRPC Java, and gRPC Kotlin stubs
            task.plugins {
                id("grpc")
                id("grpckt")
            }
            // Generate lite Kotlin stubs for Android-friendly usage
            task.builtins {
                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

// Ensure proto generation happens before packaging/publishing
tasks.named("build") {
    dependsOn("generateProto")
}
tasks.named("publishToMavenLocal") {
    dependsOn("generateProto")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "retail-admin-protos"
        }
    }
    repositories {
        mavenLocal()
    }
}