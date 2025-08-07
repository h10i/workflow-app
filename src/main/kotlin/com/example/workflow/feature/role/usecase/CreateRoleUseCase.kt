package com.example.workflow.feature.role.usecase

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.toViewDto
import com.example.workflow.feature.role.model.CreateRoleRequest
import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.service.RoleService
import org.springframework.stereotype.Service

@Service
class CreateRoleUseCase(
    private val roleService: RoleService
) {
    data class Result(
        val roleViewDto: RoleViewDto
    )

    fun execute(request: CreateRoleRequest): Result {
        val role = Role(
            name = request.name,
        )
        val savedRole = roleService.saveRole(role)
        return Result(
            roleViewDto = savedRole.toViewDto()
        )
    }
}