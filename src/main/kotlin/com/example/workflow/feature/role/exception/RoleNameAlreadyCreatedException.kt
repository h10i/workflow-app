package com.example.workflow.feature.role.exception

import com.example.workflow.common.exception.BusinessException
import com.example.workflow.core.role.Role

class RoleNameAlreadyCreatedException(
    name: String,
    code: String = "Duplicate",
    message: String = "This role name is already created."
) : BusinessException(
    field = Role::name.name,
    rejectedValue = name,
    code = code,
    message = message
)
