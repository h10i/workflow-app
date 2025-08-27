package com.example.workflow.feature.role.usecase

import com.example.workflow.feature.role.service.RoleService
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteRoleUseCase(
    private val roleService: RoleService,
) {
    fun execute(id: UUID) {
        roleService.verifyRoleIdAvailability(id)
        roleService.deleteById(id)
    }
}