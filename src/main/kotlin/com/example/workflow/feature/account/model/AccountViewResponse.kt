package com.example.workflow.feature.account.model

import java.util.*

data class AccountViewResponse(
    val id: UUID,
    val mailAddress: String,
    val roleNames: List<String>,
)