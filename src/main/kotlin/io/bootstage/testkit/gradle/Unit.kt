package io.bootstage.testkit.gradle

import kotlin.reflect.KClass

/**
 * [Unit] annotation is used for [TestUnit] running
 *
 *
 * ```kotlin
 * class IntegrationTest {
 *
 *     @Test
 *     @Unit(SimpleTestUnit::class)
 *     fun test() {
 *         ...
 *     }
 *
 * }
 * ```
 *
 * @author johnsonlee
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Unit(
        val value: KClass<out TestUnit>
)