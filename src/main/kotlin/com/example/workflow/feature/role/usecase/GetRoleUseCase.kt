package com.example.workflow.feature.role.usecase

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.toViewDto
import com.example.workflow.feature.role.exception.RoleNotFoundException
import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.service.RoleService
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetRoleUseCase(
    private val roleService: RoleService,
) {
    data class Result(
        val roleViewDto: RoleViewDto
    )

    fun execute(id: UUID): Result {
        val role: Role =
            roleService.getRoleById(id) ?: throw RoleNotFoundException(mapOf(Role::id.name to id.toString()))
        return Result(
            roleViewDto = role.toViewDto(),
        )
    }
}