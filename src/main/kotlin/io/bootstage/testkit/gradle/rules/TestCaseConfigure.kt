package io.bootstage.testkit.gradle.rules

import io.bootstage.testkit.gradle.Case
import io.bootstage.testkit.gradle.TestCase
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

private val SERVICES_PATH = arrayOf(
        "buildSrc", "src", "main", "resources", "META-INF", "services", TestCase::class.java.name
).joinToString(File.separator)

const val PLUGIN_UNDER_TEST_CLASSPATH_PROPERTIES = "plugin-under-test-classpath.properties"

/**
 * The configurator of [TestCase] which annotated by [Case]
 *
 * @author johnsonlee
 */
open class TestCaseConfigure(val projectDir: () -> File) : TestWatcher() {

    override fun starting(description: Description) {
        // configure SPI
        description.getAnnotation(Case::class.java)?.run {
            File(projectDir(), SERVICES_PATH).apply {
                parentFile.mkdirs()
            }.writeText(value.java.name)
        }
        // write extral classpath
        val injection = description.testClass.protectionDomain.codeSource.location.file.let(::File).let(::listOf)
        val classpath = injection.joinToString("\\:", "classpath=")
        File(projectDir(), PLUGIN_UNDER_TEST_CLASSPATH_PROPERTIES).writeText(classpath)
    }

}