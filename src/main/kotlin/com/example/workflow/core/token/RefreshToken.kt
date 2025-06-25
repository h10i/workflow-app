package com.example.workflow.core.token

import com.example.workflow.core.account.Account
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "refresh_token",
    uniqueConstraints = [UniqueConstraint(columnNames = ["account_id", "token_value"])]
)
data class RefreshToken(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "token_value", nullable = false)
    val value: String,

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    val account: Account,

    @Column(name = "expiry_date", nullable = false)
    val expiryDate: Instant
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RefreshToken

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "RefreshToken(id=$id, value='$value', account=${account.id}, expiryDate=$expiryDate)"
    }
}