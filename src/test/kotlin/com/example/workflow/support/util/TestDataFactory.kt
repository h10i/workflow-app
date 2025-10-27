package com.example.workflow.support.util

import com.example.workflow.core.account.Account
import com.example.workflow.core.account.AccountRole
import com.example.workflow.core.auth.RefreshToken
import com.example.workflow.core.role.Role
import com.example.workflow.core.workflow.request.RequestType
import com.example.workflow.infra.security.model.RsaKeyProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.*
import kotlin.random.Random

object TestDataFactory {
    fun createUniqueEmailAddress() = "user-${UUID.randomUUID()}@example.com"

    fun createValidTestPassword() = "P4sSw0rd!${Random.nextInt(100).toString().padStart(3, '0')}"

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

    fun createRsaKeyProperties(): RsaKeyProperties {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()
        return RsaKeyProperties(
            publicKey = keyPair.public as RSAPublicKey,
            privateKey = keyPair.private as RSAPrivateKey
        )
    }

    fun createRequestType(
        id: UUID = UUID.randomUUID(),
        name: String = UUID.randomUUID().toString(),
        description: String? = null,
        schemaDefinition: JsonNode = jacksonObjectMapper().readTree("""{"foo":"bar"}"""),
    ): RequestType = RequestType(
        id = id,
        name = name,
        description = description,
        schemaDefinition = schemaDefinition,
    )
}
