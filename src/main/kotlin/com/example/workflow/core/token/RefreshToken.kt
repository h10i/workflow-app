package com.example.workflow.core.token

import com.example.workflow.core.account.Account
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "refresh_token",
    uniqueConstraints = [UniqueConstraint(columnNames = ["account_id", "value"])]
)
data class RefreshToken(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "value", nullable = false)
    val value: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    val account: Account,

    @Column(name = "expiry_date", nullable = false)
    val expiryDate: Instant
)