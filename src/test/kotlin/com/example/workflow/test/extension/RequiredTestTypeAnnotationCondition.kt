package com.example.workflow.test.extension

import com.example.workflow.test.annotation.E2ETest
import com.example.workflow.test.annotation.IntegrationTest
import com.example.workflow.test.annotation.UnitTest
import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.reflect.KClass

class RequiredTestTypeAnnotationCondition : ExecutionCondition {
    private val allowedAnnotations: Set<KClass<out Annotation>> = setOf(
        UnitTest::class,
        IntegrationTest::class,
        E2ETest::class,
    )

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        val clazz = context.testClass.orElse(null)
            ?: return ConditionEvaluationResult.enabled("Not a test class")

        if (hasAllowedAnnotationRecursively(clazz)) {
            return ConditionEvaluationResult.enabled("Valid test type annotation present.")
        }

        throw IllegalStateException(
            """

            ❌ Test class '${clazz.name}' is missing a required test type annotation.

              ➤ One of the following annotations must be present on the class:
                 ${allowedAnnotations.joinToString("\n                 ") { "@${it.simpleName}" }}

              ➤ Example usage:
                 @UnitTest
                 class ${clazz.simpleName} { ... }

            """.trimIndent()
        )
    }

    private fun hasAllowedAnnotationRecursively(start: Class<*>): Boolean {
        var current: Class<*>? = start
        while (current != null) {
            if (allowedAnnotations.any { current.isAnnotationPresent(it.java) }) return true
            current = current.enclosingClass
        }
        return false
    }
}