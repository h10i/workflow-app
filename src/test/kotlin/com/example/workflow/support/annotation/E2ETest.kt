package com.example.workflow.support.annotation

import com.example.workflow.support.constants.TestTags
import org.junit.jupiter.api.Tag

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag(TestTags.E2E)
annotation class E2ETest()
