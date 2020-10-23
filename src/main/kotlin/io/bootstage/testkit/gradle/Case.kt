package io.bootstage.testkit.gradle

import kotlin.reflect.KClass

/**
 * [Case] annotation is used for [TestCase] running
 *
 *
 * ```kotlin
 * class IntegrationTest {
 *
 *     @Test
 *     @Unit(SimpleTestCase::class)
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
annotation class Case(
        val value: KClass<out TestCase>
)