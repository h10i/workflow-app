package com.example.workflow.feature.token.constants

import kotlin.time.Duration.Companion.days

object AuthConstants {
    const val ACCESS_TOKEN_LIFETIME_MINUTES: Long = 30L
    const val REFRESH_TOKEN_LIFETIME_DAYS: Long = 90L
    val REFRESH_TOKEN_LIFETIME_SECONDS: Long = REFRESH_TOKEN_LIFETIME_DAYS.days.inWholeSeconds
}
