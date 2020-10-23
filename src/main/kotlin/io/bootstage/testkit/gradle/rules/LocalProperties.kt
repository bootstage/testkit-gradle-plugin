package io.bootstage.testkit.gradle.rules

import com.didiglobal.booster.build.AndroidSdk
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

/**
 * The rule for `local.properties` generating
 *
 * @author johnsonlee
 */
open class LocalProperties(val projectDir: () -> File) : TestWatcher() {

    override fun starting(description: Description) {
        File(projectDir(), "local.properties").writeText("sdk.dir=${AndroidSdk.getLocation()}")
    }

}