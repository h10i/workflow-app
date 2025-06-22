package com.example.workflow.common.exception

class UnauthorizedException(
    message: String = "Invalid credentials."
) : RuntimeException(message)