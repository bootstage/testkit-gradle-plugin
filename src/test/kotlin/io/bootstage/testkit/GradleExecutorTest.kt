package io.bootstage.testkit

import io.bootstage.testkit.gradle.rules.GradleExecutor
import io.bootstage.testkit.gradle.rules.copyFromResource
import io.bootstage.testkit.gradle.rules.rule
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import kotlin.test.Test

class GradleExecutorTest {

    private val projectDir = TemporaryFolder()

    @get:Rule
    val chain: TestRule = rule(projectDir) {
        GradleExecutor(projectDir::getRoot)
    }

    @Test
    fun `test gradle build`() {
        projectDir.copyFromResource("build.gradle")
        projectDir.copyFromResource("src")
    }

}