package io.bootstage.testkit.gradle

import io.bootstage.testkit.gradle.rules.PLUGIN_UNDER_TEST_CLASSPATH_PROPERTIES
import io.bootstage.testkit.gradle.util.ServiceLoaderLite
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GUtil
import java.io.File
import java.net.URLClassLoader

/**
 * Plugin for Gradle plugin project testing
 *
 * @author johnsonlee
 */
class TestKitPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val properties = GUtil.loadProperties(project.file(PLUGIN_UNDER_TEST_CLASSPATH_PROPERTIES))
        val classpath = properties.getProperty("classpath").split(File.pathSeparator).map {
            File(it).toURI().toURL()
        }.toTypedArray()
        val classLoader = URLClassLoader(classpath, project.buildscript.classLoader)
        classpath.forEach(::println)
        ServiceLoaderLite.loadImplementations(TestCase::class.java, classLoader).forEach {
            it.apply(project)
        }
    }

}