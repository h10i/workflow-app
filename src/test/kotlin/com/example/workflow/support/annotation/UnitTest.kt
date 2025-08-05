package com.example.workflow.support.annotation

import com.example.workflow.support.constants.TestTags
import org.junit.jupiter.api.Tag

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag(TestTags.UNIT)
annotation class UnitTest
