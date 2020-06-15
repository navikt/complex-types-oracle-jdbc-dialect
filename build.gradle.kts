import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    // Apply the java-library plugin to add support for Java Library
    `java-library`
    id("maven-publish")
}

group = "com.github.navikt"
version = (if (properties["version"] != null && properties["version"] != "unspecified") properties["version"] else "local-build")!!

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    maven {
        // Look for POMs and artifacts, such as JARs, here
        url = uri("http://packages.confluent.io/maven/")
    }
}

dependencies {
    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api("org.apache.commons:commons-math3:3.6.1")

    implementation("org.apache.kafka", "kafka-clients", "2.4.1")
    implementation("org.apache.kafka", "connect-api", "2.4.1")
    implementation("io.confluent:kafka-connect-jdbc:5.4.1")
    implementation("com.datamountaineer:kafka-connect-common:1.1.9")

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation("com.google.guava:guava:28.1-jre")

    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

val test by tasks.getting(Test::class) {
    // Use junit platform for unit tests
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showStackTraces = true
        showCauses = true
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}


tasks.withType<Wrapper> {
    gradleVersion = "6.4.1"
    distributionType = Wrapper.DistributionType.ALL
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val githubUser: String? by project
val githubPassword: String? by project

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/navikt/complex-types-oracle-jdbc-dialect")
            credentials {
                username = githubUser
                password = githubPassword
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {

            pom {
                name.set("complex-types-oracle-jdbc-dialect")
                description.set("Oracle JDBC dialect implementation with support for Complex types (STRUCT) in Kafka Connect")
                url.set("https://github.com/navikt/complex-types-oracle-jdbc-dialect")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/navikt/complex-types-oracle-jdbc-dialect.git")
                    developerConnection.set("scm:git:https://github.com/navikt/complex-types-oracle-jdbc-dialect.git")
                    url.set("https://github.com/navikt/complex-types-oracle-jdbc-dialect")
                }
            }
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}
