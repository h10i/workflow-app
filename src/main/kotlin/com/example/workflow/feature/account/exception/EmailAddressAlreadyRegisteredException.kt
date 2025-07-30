package com.example.workflow.feature.account.exception

import com.example.workflow.common.exception.BusinessException
import com.example.workflow.core.account.Account

class EmailAddressAlreadyRegisteredException(
    message: String = "This email address is already registered."
) : BusinessException(field = Account::emailAddress.name, message = message)