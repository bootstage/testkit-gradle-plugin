import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date

plugins {
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
    id("org.jetbrains.dokka") version "1.4.10"
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("com.jfrog.bintray") version "1.8.5"
}

group = "io.bootstage.testkit"
version = "0.1.0"
description = "Test kit for Gradle plugin testing"

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

    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.10")
}

gradlePlugin {
    plugins {
        create("gradleTestPlugin") {
            id = "io.bootstage.testkit"
            implementationClass = "io.bootstage.testkit.gradle.TestKitPlugin"
        }
    }
}


val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val generateJavadoc by tasks.creating(Jar::class) {
    dependsOn("dokkaHtml")
    group = "jar"
    archiveClassifier.set("javadoc")
    from(tasks["dokkaHtml"].property("outputDirectory"))
}

tasks {

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                groupId = "${project.group}"
                artifactId = project.name
                version = "${project.version}"

                from(getComponents()["java"])

                artifact(sourcesJar)
                artifact(generateJavadoc)

                pom.withXml {
                    asNode().apply {
                        appendNode("name", project.name)
                        appendNode("description", "${project.description}")
                        appendNode("url", "https://github.com/bootstage/${project.name}")
                        appendNode("licenses").appendNode("license").apply {
                            appendNode("name", "Apache-2.0")
                            appendNode("url", "https://www.apache.org/licenses/LICENSE-2.0.txt")
                            appendNode("distribution", "repo")
                        }
                        appendNode("developers").appendNode("developer").apply {
                            appendNode("id", "johnsonlee")
                            appendNode("name", "Johnson Lee")
                        }
                        appendNode("scm").apply {
                            appendNode("url", "https://github.com/bootstage/${project.name}")
                            appendNode("connection", "scm:git:git://github.com/bootstage/${project.name}.git")
                            appendNode("developerConnection", "scm:git:git@github.com:bootstage/${project.name}.git")
                        }
                    }
                }
            }
        }
    }
}

bintray {
    user = "${project.findProperty("BINTRAY_USER") ?: System.getenv("BINTRAY_USER")}"
    key = "${project.findProperty("BINTRAY_KEY") ?: System.getenv("BINTRAY_KEY")}"
    publish = true

    pkg.apply {
        repo = "testkit"
        name = project.name
        userOrg = "bootstage"
        githubRepo = "bootstage/${project.name}"
        vcsUrl = "https://github.com/bootstage/${project.name}"
        desc = project.description
        description = project.description
        setLabels("kotlin", "gradle", "testing", "testkit")
        setLicenses("Apache-2.0")
        websiteUrl = "https://bootstage.io/testkit-gradle"
        issueTrackerUrl = "https://github.com/bootstage/${project.name}/issues"
        githubReleaseNotesFile = "README.md"

        version.apply {
            name = "${project.version}"
            desc = project.description
            released = "${Date()}"
            vcsTag = "${project.version}"
        }
    }
}
