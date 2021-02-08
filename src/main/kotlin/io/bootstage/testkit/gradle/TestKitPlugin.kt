package io.bootstage.testkit.gradle

import io.bootstage.testkit.gradle.rules.PLUGIN_UNDER_TEST_CLASSPATH_PROPERTIES
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GUtil
import java.io.File
import java.net.URLClassLoader
import java.util.ServiceLoader

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

        val classLoader = if (project.buildscript.classLoader is URLClassLoader) {
            URLClassLoader(classpath + (project.buildscript.classLoader as URLClassLoader).urLs, Thread.currentThread().contextClassLoader)
        } else {
            URLClassLoader(classpath, Thread.currentThread().contextClassLoader)
        }

        ServiceLoader.load(TestCase::class.java, classLoader).asIterable().toList().takeIf(Collection<TestCase>::isNotEmpty)?.forEach {
            it.apply(project)
        } ?: throw GradleException("No TestCase found")
    }

}