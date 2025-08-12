package com.example.workflow.feature.role.exception

import com.example.workflow.common.exception.BusinessException
import com.example.workflow.core.role.Role

class RoleNameAlreadyCreatedException(
    message: String = "This role name is already created."
) : BusinessException(field = Role::name.name, message = message)