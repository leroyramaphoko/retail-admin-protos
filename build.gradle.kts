import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "2.0.21"
    `maven-publish`
    id("com.google.protobuf") version "0.9.4"
}

group = "com.github.leroyramaphoko"
version = "1.0.42"

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.protobuf:protobuf-kotlin:3.25.5")
    api("io.grpc:grpc-kotlin-stub:1.4.1")
    api("io.grpc:grpc-protobuf:1.69.0")
    api("javax.annotation:javax.annotation-api:1.3.2")
}

kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.25.5" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.69.0" }
        id("grpckt") { artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar" }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
                id("grpckt")
            }
            task.builtins {
                create("kotlin")
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