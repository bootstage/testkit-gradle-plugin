package io.bootstage.testkit.gradle

import org.gradle.api.Project

/**
 * Test case for gradle project testing
 *
 * @author johnsonlee
 */
interface TestCase {

    /**
     * Apply the [project] to this test unit
     */
    fun apply(project: Project)

}