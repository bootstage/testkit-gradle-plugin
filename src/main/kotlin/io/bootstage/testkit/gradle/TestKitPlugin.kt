package io.bootstage.testkit.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.ServiceLoader

/**
 * Plugin for Gradle plugin project testing
 *
 * @author johnsonlee
 */
class TestKitPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        ServiceLoader.load(TestUnit::class.java, project.buildscript.classLoader).forEach {
            it.apply(project)
        }
    }

}