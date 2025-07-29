package com.example.workflow.feature.account.exception

class EmailAddressAlreadyRegisteredException(
    message: String = "This email address is already registered."
) : RuntimeException(message)