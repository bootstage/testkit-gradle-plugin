package io.bootstage.testkit

import io.bootstage.testkit.gradle.rules.GradleExecutor
import io.bootstage.testkit.gradle.rules.rule
import io.bootstage.testkit.gradle.rules.copyFromResource
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import kotlin.test.Test

class GradleExecutorTest {

    private val projectDir = TemporaryFolder()

    @get:Rule
    val chain: RuleChain = rule(projectDir) {
        GradleExecutor(projectDir::getRoot)
    }

    @Test
    fun `test gradle build`() {
        projectDir.copyFromResource("build.gradle")
        projectDir.copyFromResource("src")
    }

}