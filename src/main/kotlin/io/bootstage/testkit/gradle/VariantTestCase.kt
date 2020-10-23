package io.bootstage.testkit.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project

/**
 * Test unit in Android build variant scope
 *
 * @author johnsonlee
 */
abstract class VariantTestCase : TestCase {

    override fun apply(project: Project) {
        project.afterEvaluate {
            when (val android = project.getAndroid<BaseExtension>()) {
                is AppExtension -> android.applicationVariants.forEach(::apply)
                is LibraryExtension -> android.libraryVariants.forEach(::apply)
                else -> TODO("Unsupported extension type: ${android.javaClass}")
            }
        }
    }

    abstract fun apply(variant: BaseVariant)

}

private inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T
