package com.example.workflow.feature.account.model

import java.util.*

data class AccountViewDto(
    val id: UUID,
    val mailAddress: String,
    val roleNames: List<String>,
)

fun AccountViewDto.toViewResponse(): AccountViewResponse = AccountViewResponse(
    id = id,
    mailAddress = mailAddress,
    roleNames = roleNames,
)
