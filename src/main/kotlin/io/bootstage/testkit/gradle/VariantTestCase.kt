package io.bootstage.testkit.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

/**
 * Test unit in Android build variant scope
 *
 * @author johnsonlee
 */
abstract class VariantTestCase : TestCase {

    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants {variant ->
            project.afterEvaluate {
                apply(variant)
            }
        }
    }

    abstract fun apply(variant: Variant)

}

private inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T
