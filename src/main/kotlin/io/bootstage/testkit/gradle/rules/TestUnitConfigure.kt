package io.bootstage.testkit.gradle.rules

import io.bootstage.testkit.gradle.TestUnit
import io.bootstage.testkit.gradle.Unit
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

private val SERVICES_PATH = arrayOf(
        "buildSrc", "src", "main", "resources", "META-INF", "services", TestUnit::class.java.name
).joinToString(File.separator)

/**
 * The configurator of [TestUnit] which annotated by [Unit]
 *
 * @author johnsonlee
 */
open class TestUnitConfigure(val projectDir: () -> File) : TestWatcher() {

    override fun starting(description: Description) {
        // configure SPI
        description.getAnnotation(Unit::class.java)?.run {
            File(projectDir(), SERVICES_PATH).apply {
                parentFile.mkdirs()
            }.writeText(value.java.name)
        }
    }

}