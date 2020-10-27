import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
//  `java-gradle-plugin`
    `maven-publish`
    `signing`

    id("com.gradle.plugin-publish") version "0.12.0"
    id("de.marcphilipp.nexus-publish") version "0.4.0"
    id("io.codearte.nexus-staging") version "0.21.2"
    id("org.jetbrains.dokka") version "1.4.10"

    kotlin("jvm") version embeddedKotlinVersion
}

group = "io.bootstage.testkit"
version = "1.0.0"
description = "Testkit plugin for custom Android Gradle plugin testing"

repositories {
    mavenLocal()
    google()
    mavenCentral()
    jcenter()
}

dependencies {
    api(gradleTestKit())
    api("org.jetbrains.kotlin:kotlin-test")
    api("org.jetbrains.kotlin:kotlin-test-junit")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.didiglobal.booster:booster-build:2.4.0")
    compileOnly("com.android.tools.build:gradle:3.0.0")
    testCompileOnly("com.android.tools.build:gradle:3.0.0")

    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.10")
}

val OSSRH_USERNAME = project.properties["OSSRH_USERNAME"] as? String ?: System.getenv("OSSRH_USERNAME")
val OSSRH_PASSWORD = project.properties["OSSRH_PASSWORD"] as? String ?: System.getenv("OSSRH_PASSWORD")

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val generateDokkaHtml by tasks.registering(Copy::class) {
    dependsOn("dokkaHtml")
    from(tasks["dokkaHtml"].property("outputDirectory"))
    into(project.file("docs"))
}

val generateJavadoc by tasks.registering(Jar::class) {
    dependsOn("generateDokkaHtml")
    group = "jar"
    archiveClassifier.set("javadoc")
    from(tasks["dokkaHtml"].property("outputDirectory"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(OSSRH_USERNAME)
            password.set(OSSRH_PASSWORD)
        }
    }
}

nexusStaging {
    packageGroup = "io.bootstage"
    username = OSSRH_USERNAME
    password = OSSRH_PASSWORD
    numberOfRetries = 50
    delayBetweenRetriesInMillis = 3000
}

publishing {
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${project.group}"
            artifactId = project.name
            version = "${project.version}"

            from(components["java"])

            artifact(sourcesJar.get())
            artifact(generateJavadoc.get())

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/bootstage/${project.name}")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("johnsonlee")
                        name.set("Johnson Lee")
                        email.set("g.johnsonlee@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/bootstage/${project.name}.git")
                    developerConnection.set("scm:git:git@github.com:bootstage/${project.name}.git")
                    url.set("https://github.com/bootstage/${project.name}")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

//gradlePlugin {
//    plugins {
//        create("testkitPlugin") {
//            id = "io.bootstage.testkit"
//            implementationClass = "io.bootstage.testkit.gradle.TestKitPlugin"
//        }
//    }
//}
