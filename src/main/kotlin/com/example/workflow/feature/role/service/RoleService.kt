package com.example.workflow.feature.role.service

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.RoleRepository
import com.example.workflow.feature.role.exception.RoleNameAlreadyCreatedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {
    @Transactional
    fun saveRole(role: Role): Role {
        return roleRepository.save(role)
    }

    @Transactional
    fun getAllRoles(): List<Role> {
        return roleRepository.findAll()
    }

    @Transactional
    fun verifyRoleAvailability(name: String) {
        if (roleRepository.findByName(name) != null) {
            throw RoleNameAlreadyCreatedException()
        }
    }
}
