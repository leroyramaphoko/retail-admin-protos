plugins {
    kotlin("jvm") version "2.2.21"
    `maven-publish`
    id("com.google.protobuf") version "0.9.4"
}

group = "com.apptorise.retail.core.api"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.25.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.25.1")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
    withJavadocJar()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                // Java is enabled by default, no need to call 'id' or 'create'
                create("kotlin")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "retail-admin-protos"

            pom {
                name.set("Retail Admin Protos")
                description.set("Protocol Buffer library for retail administration")
                url.set("https://github.com/apptorise/retail-admin-protos")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}