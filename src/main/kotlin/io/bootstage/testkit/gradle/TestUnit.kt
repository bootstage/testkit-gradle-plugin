package io.bootstage.testkit.gradle

import org.gradle.api.Project

/**
 * Test unit for gradle project testing
 *
 * @author johnsonlee
 */
interface TestUnit {

    /**
     * Apply the [project] to this test unit
     */
    fun apply(project: Project)

}