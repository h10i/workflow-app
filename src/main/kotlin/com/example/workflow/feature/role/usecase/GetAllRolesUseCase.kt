package com.example.workflow.feature.role.usecase

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.toViewDto
import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.service.RoleService
import org.springframework.stereotype.Service

@Service
class GetAllRolesUseCase(
    private val roleService: RoleService,
) {
    data class Result(
        val roleViewDtoList: List<RoleViewDto>
    )

    fun execute(): Result {
        val roles: List<Role> = roleService.getAllRoles()
        val roleViewDtoList: List<RoleViewDto> = roles.map { it.toViewDto() }
        return Result(
            roleViewDtoList = roleViewDtoList,
        )
    }
}