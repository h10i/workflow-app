package com.example.workflow.feature.account.exception

import com.example.workflow.common.exception.BusinessException
import com.example.workflow.core.account.Account

class EmailAddressAlreadyRegisteredException(
    emailAddress: String,
    code: String = "Duplicate",
    message: String = "This email address is already registered."
) : BusinessException(
    field = Account::emailAddress.name,
    rejectedValue = emailAddress,
    code = code,
    message = message
)
