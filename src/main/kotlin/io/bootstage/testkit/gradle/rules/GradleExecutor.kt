package io.bootstage.testkit.gradle.rules

import io.bootstage.testkit.gradle.Case
import io.bootstage.testkit.gradle.TestCase
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
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

    private var debug: Boolean = false
    private var forwardOutput: Boolean = false
    private var forwardStdout: Boolean = false
    private var forwardStderr: Boolean = true

    fun withDebug(debug: Boolean) = apply {
        this.debug = debug
    }

    fun forwardOutput() = apply {
        this.forwardOutput = true
        this.forwardStdout = false
        this.forwardStderr = false
    }

    fun forwardStdOutput() = apply {
        this.forwardStdout = true
    }

    fun forwardStdError() = apply {
        this.forwardStderr = true
    }

    override fun starting(description: Description) {
        // configure SPI
        description.getAnnotation(Case::class.java)?.run {
            File(projectDir(), SERVICES_PATH).apply {
                parentFile.mkdirs()
            }.writeText(value.java.name)
        }
        // write extra classpath
        val injection = description.testClass.protectionDomain.codeSource.location.file.let(::File).let(::listOf)
        val classpath = injection.joinToString("\\:", "classpath=")
        File(projectDir(), PLUGIN_UNDER_TEST_CLASSPATH_PROPERTIES).writeText(classpath)
    }

    override fun finished(description: Description) {
        val result = GradleRunner.create().apply {
            withArguments(*args)
            withDebug(debug)
            withGradleVersion(gradleVersion)
            withPluginClasspath()
            withProjectDir(projectDir())

            if (forwardOutput) {
                forwardOutput()
            } else {
                if (forwardStdout) {
                    forwardStdOutput(PrintWriter(System.out, true))
                }
                if (forwardStderr) {
                    forwardStdError(PrintWriter(System.err, true))
                }
            }
        }.build()

        if (result.tasks.any { it.outcome == TaskOutcome.FAILED }) {
            fail()
        }
    }

}

private val SERVICES_PATH = arrayOf(
        "buildSrc", "src", "main", "resources", "META-INF", "services", TestCase::class.java.name
).joinToString(File.separator)

internal const val PLUGIN_UNDER_TEST_CLASSPATH_PROPERTIES = "plugin-under-test-classpath.properties"
