package com.example.workflow.testconfig

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration
import java.time.Instant
import java.time.temporal.ChronoUnit

object AssertJComparisonConfig {
    val INSTANT_TRUNCATED_TO_MILLIS: RecursiveComparisonConfiguration =
        RecursiveComparisonConfiguration.builder()
            .withComparatorForType(
                Comparator { inst1: Instant, inst2: Instant ->
                    if (inst1.truncatedTo(ChronoUnit.MILLIS) == inst2.truncatedTo(ChronoUnit.MILLIS)) {
                        0
                    } else {
                        inst1.compareTo(inst2)
                    }
                },
                Instant::class.java
            )
            .build()
}