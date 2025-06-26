package com.example.workflow.testutil

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRole
import com.example.workflow.core.role.Role
import com.example.workflow.core.token.RefreshToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
        )
    }

    fun createAccount(
        id: UUID = UUID.randomUUID(),
        emailAddress: String = "test@example.com",
        password: String = "test-password",
        roles: List<Role> = listOf(),
        refreshTokenValues: List<String> = listOf(),
    ): Account {
        val account = Account(
            id = id,
            emailAddress = emailAddress,
            password = password,
            roles = mutableListOf(),
            refreshTokens = mutableListOf()
        )

        roles.forEach { role ->
            registerAccountRole(account = account, role = role)
        }

        refreshTokenValues.forEach { refreshTokenValue ->
            registerRefreshToken(account = account, value = refreshTokenValue)
        }

        return account
    }

    fun registerAccountRole(
        id: UUID = UUID.randomUUID(),
        account: Account,
        role: Role
    ): AccountRole {
        val accountRole = AccountRole(
            id = id,
            account = account,
            role = role
        )
        account.roles.add(accountRole)
        role.accountRoles.add(accountRole)
        return accountRole
    }

    fun registerRefreshToken(
        id: UUID = UUID.randomUUID(),
        value: String = UUID.randomUUID().toString(),
        account: Account,
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

    fun createAuthentication(
        account: Account = createAccount(
            roles = listOf(createRole())
        )
    ): Authentication {
        val authorities = account.roles.map { SimpleGrantedAuthority(it.role.name) }
        return UsernamePasswordAuthenticationToken(account.id.toString(), null, authorities)
    }
}