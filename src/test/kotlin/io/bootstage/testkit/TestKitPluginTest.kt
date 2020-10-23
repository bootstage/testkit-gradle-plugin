package io.bootstage.testkit

import io.bootstage.testkit.gradle.TestCase
import io.bootstage.testkit.gradle.Case
import io.bootstage.testkit.gradle.rules.GradleExecutor
import io.bootstage.testkit.gradle.rules.TestCaseConfigure
import io.bootstage.testkit.gradle.rules.copyFromResource
import io.bootstage.testkit.gradle.rules.rule
import org.gradle.api.Project
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import kotlin.test.Test

class TestKitPluginTest {

    private val projectDir: TemporaryFolder = TemporaryFolder()

    @get:Rule
    val chain: TestRule = rule(projectDir) { projectDir ->
        val root = projectDir::getRoot
        rule(TestCaseConfigure(root)) {
            GradleExecutor(root)
        }
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