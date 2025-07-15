package com.example.workflow.test.annotation

import com.example.workflow.test.constants.TestTags
import org.junit.jupiter.api.Tag

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag(TestTags.E2E)
annotation class E2ETest()
