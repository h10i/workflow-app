package com.example.workflow.core.account

import com.example.workflow.core.token.RefreshToken
import com.example.workflow.feature.account.model.AccountViewDto
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "account")
data class Account(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "email_address", nullable = false, unique = true)
    var emailAddress: String,

    @Column(name = "password", nullable = false)
    var password: String,

    @OneToMany(mappedBy = "account", cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
    val roles: MutableList<AccountRole> = mutableListOf(),

    @OneToMany(mappedBy = "account", cascade = [CascadeType.REMOVE])
    val refreshTokens: MutableList<RefreshToken> = mutableListOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Account(id=$id, emailAddress='$emailAddress', password='$password', roles=$roles, refreshTokens=$refreshTokens)"
    }
}

fun Account.toViewDto(): AccountViewDto = AccountViewDto(
    id = id,
    emailAddress = emailAddress,
    roleNames = roles.map { it.role.name }
)
