plugins {
    id("maven-publish")
    id("io.spring.dependency-management") version "1.1.4"
}


group = "com.snowcode.lab"
version = "0.0.1-SNAPSHOT"

extra["springBootVersion"] = "3.2.3"
extra["springCloudVersion"] = "2023.0.0"
extra["mapstructVersion"] = "1.5.5.Final"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${extra["springBootVersion"]}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${extra["springCloudVersion"]}")
    }
    dependencies {
        dependency("org.mapstruct:mapstruct:${extra["mapstructVersion"]}")
        dependency("org.mapstruct:mapstruct-processor:${extra["mapstructVersion"]}")
    }
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            versionMapping {
                allVariants {
                    fromResolutionResult()
                }
            }
        }

    }

    repositories {
        maven {
            url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                uri("s3://snow-code-lab-maven/maven-snapshots")
            } else {
                uri("s3://snow-code-lab-maven/maven-releases")
            }

            credentials(AwsCredentials::class.java) {
                accessKey =
                    System.getenv("AWS_ACCESS_KEY_ID") ?: project.findProperty("aws.accessKeyId") as? String
                            ?: ""
                secretKey =
                    System.getenv("AWS_SECRET_ACCESS_KEY")
                        ?: project.findProperty("aws.secretAccessKey") as? String
                                ?: ""
            }
        }
    }
}