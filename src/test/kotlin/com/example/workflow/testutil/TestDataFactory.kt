package com.example.workflow.testutil

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRole
import com.example.workflow.core.role.Role
import com.example.workflow.core.token.RefreshToken
import java.time.Instant
import java.util.*

object TestDataFactory {
    fun createRole(
        id: UUID = UUID.randomUUID(),
        name: String = "USER"
    ): Role {
        return Role(
            id = id,
            name = name,
            accountRoles = mutableListOf()
        )
    }

    fun createAccount(
        id: UUID = UUID.randomUUID(),
        emailAddress: String = "test@example.com",
        password: String = "hashed-password",
        roles: List<Role> = listOf(createRole())
    ): Account {
        val account = Account(
            id = id,
            emailAddress = emailAddress,
            password = password,
            roles = mutableListOf(),
            refreshTokens = mutableListOf()
        )

        roles.forEach { role ->
            val accountRole = createAccountRole(account = account, role = role)
            account.roles.add(accountRole)
            role.accountRoles.add(accountRole)
        }

        return account
    }

    fun createAccountRole(
        id: UUID = UUID.randomUUID(),
        account: Account,
        role: Role
    ): AccountRole {
        return AccountRole(
            id = id,
            account = account,
            role = role
        )
    }

    fun createRefreshToken(
        id: UUID = UUID.randomUUID(),
        value: String = "refresh-token-value",
        account: Account = createAccount(),
        expiryDate: Instant = Instant.now().plusSeconds(3600)
    ): RefreshToken {
        val token = RefreshToken(
            id = id,
            value = value,
            account = account,
            expiryDate = expiryDate
        )
        account.refreshTokens.add(token)
        return token
    }
}