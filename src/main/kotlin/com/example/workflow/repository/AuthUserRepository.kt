package com.example.workflow.repository

import com.example.workflow.model.AuthUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AuthUserRepository : JpaRepository<AuthUser, UUID> {
    fun findByMailAddress(mailAddress: String): AuthUser?
}