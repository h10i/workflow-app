package com.example.workflow.feature.role.exception

import com.example.workflow.common.exception.ResourceNotFoundException

class RoleNotFoundException(
    searchCriteria: Map<String, String>
) : ResourceNotFoundException(resourceName = "Role", searchCriteria = searchCriteria)