package io.bootstage.testkit.gradle.rules

import org.junit.rules.RuleChain
import org.junit.rules.TestRule

/**
 * build [TestRule] by [RuleChain], for example:
 *
 * ```kotlin
 * val testRule = rule(OuterRule()) {
 *     rule(MiddleRule()) {
 *         InnerRule()
 *     }
 * }
 * ```
 */
inline fun <reified T : TestRule> rule(rule: T, block: (T) -> TestRule): TestRule {
    return RuleChain.outerRule(rule).around(block(rule))
}
