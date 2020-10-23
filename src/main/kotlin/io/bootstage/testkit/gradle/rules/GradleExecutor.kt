package io.bootstage.testkit.gradle.rules

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.util.GradleVersion
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import kotlin.test.fail

/**
 * The [GradleExecutor] rule applies gradle runner to all test methods.
 *
 * For example:
 *
 * ```kotlin
 * class GradleIntegrationTest {
 *
 *     private val projectDir = TemporaryFolder()
 *
 *     @get:Rule
 *     val chain: RuleChain = rule(projectDir) {
 *         rule(GradleExecutor(projectDir::getRoot))
 *     }
 *
 *     @Test
 *     fun `gradle build`() {
 *         projectDir.copyFromResource("build.gradle")
 *         projectDir.copyFromResource("src")
 *     }
 *
 * }
 * ```
 *
 * @author johnsonlee
 */
open class GradleExecutor(
        val projectDir: () -> File,
        val gradleVersion: String = GradleVersion.current().version,
        vararg val args: String = arrayOf("build")
) : TestWatcher() {

    override fun finished(description: Description) {
        val result = GradleRunner.create()
                .withArguments(*args)
                .withGradleVersion(gradleVersion)
                .withPluginClasspath()
                .withProjectDir(projectDir())
                .withTestKitDir(File(System.getProperty("user.home"), ".gradle"))
                .forwardOutput()
                .build()
        if (result.tasks.any { it.outcome == TaskOutcome.FAILED }) {
            fail()
        }
    }

}
