package com.example.workflow.feature.role.service

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.RoleRepository
import com.example.workflow.feature.role.exception.RoleNameAlreadyCreatedException
import com.example.workflow.feature.role.exception.RoleNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {
    @Transactional
    fun saveRole(role: Role): Role {
        return roleRepository.save(role)
    }

    @Transactional
    fun getRoleById(id: UUID): Role? {
        return roleRepository.findById(id).orElse(null)
    }

    @Transactional
    fun getAllRoles(): List<Role> {
        return roleRepository.findAll()
    }

    @Transactional
    fun deleteById(id: UUID) {
        roleRepository.deleteById(id)
    }

    @Transactional
    fun verifyRoleIdAvailability(id: UUID) {
        if (getRoleById(id) == null) {
            throw RoleNotFoundException(mapOf(Role::id.name to id.toString()))
        }
    }

    @Transactional
    fun verifyRoleNameAvailability(name: String) {
        if (roleRepository.findByName(name) != null) {
            throw RoleNameAlreadyCreatedException()
        }
    }
}
