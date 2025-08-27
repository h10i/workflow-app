package com.example.workflow.feature.role.service

import com.example.workflow.core.role.Role
import com.example.workflow.core.role.RoleRepository
import com.example.workflow.feature.role.exception.RoleNameAlreadyCreatedException
import jakarta.persistence.EntityNotFoundException
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
            throw EntityNotFoundException("Role not found: $id")
        }
    }

    @Transactional
    fun verifyRoleAvailability(name: String) {
        if (roleRepository.findByName(name) != null) {
            throw RoleNameAlreadyCreatedException()
        }
    }
}
