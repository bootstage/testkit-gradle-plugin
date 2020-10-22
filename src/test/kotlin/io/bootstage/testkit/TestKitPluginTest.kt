package io.bootstage.testkit

import io.bootstage.testkit.gradle.TestUnit
import io.bootstage.testkit.gradle.Unit
import io.bootstage.testkit.gradle.rules.GradleExecutor
import io.bootstage.testkit.gradle.rules.TestUnitConfigure
import io.bootstage.testkit.gradle.rules.copyFromResource
import io.bootstage.testkit.gradle.rules.rule
import org.gradle.api.Project
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import kotlin.test.Test

class TestKitPluginTest {

    private val projectDir: TemporaryFolder = TemporaryFolder()

    @get:Rule
    val chain: RuleChain = rule(projectDir) { projectDir ->
        val root = projectDir::getRoot
        rule(TestUnitConfigure(root)) {
            GradleExecutor(root)
        }
    }

    @Test
    @Unit(SimpleTestUnit::class)
    fun `test gradle build`() {
        projectDir.copyFromResource("build.gradle")
        projectDir.copyFromResource("src")
    }

}

class SimpleTestUnit : TestUnit {
    override fun apply(project: Project) {
        project.projectDir.walkTopDown().forEach(::println)
    }
}