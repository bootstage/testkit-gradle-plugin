package io.bootstage.testkit.gradle.rules

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading
import org.gradle.util.GradleVersion
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.io.PrintWriter
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
        val classpath = PluginUnderTestMetadataReading.readImplementationClasspath()
        val additional = description.testClass.protectionDomain.codeSource.location?.file?.let(::File)?.let(::listOf)
                ?: emptyList()
        val result = GradleRunner.create()
                .withArguments(*args)
                .withGradleVersion(gradleVersion)
                .withPluginClasspath(classpath + additional)
                .withProjectDir(projectDir())
                .forwardStdError(PrintWriter(System.err, true))
                .forwardStdOutput(PrintWriter(System.out, true))
                .build()
        if (result.tasks.any { it.outcome == TaskOutcome.FAILED }) {
            fail()
        }
    }

}
