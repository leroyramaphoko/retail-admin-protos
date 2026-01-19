import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "2.0.21"
    `maven-publish`
    id("com.google.protobuf") version "0.9.4"
}

group = "com.github.leroyramaphoko"
version = "1.0.26"

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

// 1. Define generated paths as variables for consistency
val grpcJavaDir = "build/generated/source/proto/main/grpc"
val grpcKotlinDir = "build/generated/source/proto/main/grpckt"
val protoJavaDir = "build/generated/source/proto/main/java"
val protoKotlinDir = "build/generated/source/proto/main/kotlin"

sourceSets {
    main {
        // 2. Register these as source directories so the compiler finds them
        java.srcDirs(protoJavaDir, grpcJavaDir)
        kotlin.srcDirs(protoKotlinDir, grpcKotlinDir)
    }
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
            task.plugins {
                id("grpc")
                id("grpckt")
            }
            task.builtins {
                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

// 3. Ensure generation happens before ANY compilation
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("generateProto")
}

java {
    withSourcesJar()
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