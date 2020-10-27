## Introduction

Android developers might be use Android Gradle plugin's internal API to build custom gradle plugins for Android project, the idea is great, but the internal API of Android Gradle plugin is unstable, in fact, the internal API of each major release of Android Gradle plugin always has significant changes, It takes too much efforts on compatibility testing.

The goal of [testkit-gradle-plugin](https://plugins.gradle.org/plugin/io.bootstage.testkit) is to make the custom Android gradle plugin testing easier and more efficient

## Getting Started

### Create gradle project structure

Create Gradle project structure under Java resources directory

```
src/integrationTest/resources
├── build.gradle
└── src
    └── main
        └── java
            └── main.kt
```

#### build.gradle

```gradle
plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.3.72'
    id 'io.bootstage.testkit' version '1.0.0'
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.3.72"
}

compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

compileTestKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

apply from: "$rootDir/gradle/integration-test.gradle"

```

### gradle/integration-test.gradle

```gradle
sourceSets {
    integrationTest {
        java {
            srcDirs += []
        }
        kotlin {
            srcDirs += ['src/integrationTest/kotlin', 'src/integrationTest/java']
        }
        resources.srcDir file('src/integrationTest/resources')
        compileClasspath += sourceSets.main.output + configurations.testRuntime
        runtimeClasspath += output + compileClasspath
    }
}

configurations {
    integrationTestImplementation.extendsFrom testImplementation
    integrationTestRuntimeOnly.extendsFrom testRuntimeOnly
}

task integrationTest(type: Test) {
    description = 'Runs the integration tests.'
    group = 'verification'
    testClassesDirs = sourceSets.integrationTest.output.classesDirs
    classpath = sourceSets.integrationTest.runtimeClasspath
    mustRunAfter test
}

check.dependsOn integrationTest

compileIntegrationTestKotlin {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8
}

gradlePlugin {
    testSourceSets sourceSets.integrationTest
}
```

### Write test code

```kotlin
class SimpleIntegrationTest {

    private val projectDir: TemporaryFolder = TemporaryFolder()

    @get:Rule
    val chain: RuleChain = rule(projectDir) { projectDir ->
        GradleExecutor(projectDir::getRoot)
    }

    @Test
    @Case(SimpleTestCase::class)
    fun `test gradle build`() {
        projectDir.copyFromResource("build.gradle")
        projectDir.copyFromResource("src")
    }

}

class SimpleTestCase : TestCase {
    override fun apply(project: Project) {
        project.projectDir.walkTopDown().forEach(::println)
    }
}
```

### Run tests

Running test by executing the following command:

```bash
./gradlew cleanTest integrationTest
```
