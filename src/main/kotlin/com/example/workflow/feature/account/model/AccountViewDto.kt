package com.example.workflow.feature.account.model

import java.util.*

data class AccountViewDto(
    val id: UUID,
    val emailAddress: String,
    val roleNames: List<String>,
)

fun AccountViewDto.toViewResponse(): AccountViewResponse = AccountViewResponse(
    id = id,
    emailAddress = emailAddress,
    roleNames = roleNames,
)
