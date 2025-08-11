package com.example.workflow.core.role

import com.example.workflow.core.account.AccountRole
import com.example.workflow.feature.role.model.RoleViewDto
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "role")
data class Role(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = true, unique = true)
    val name: String,

    @OneToMany(mappedBy = "role", cascade = [CascadeType.REMOVE])
    val accountRoles: MutableList<AccountRole> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Role

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Role(id=$id, name='$name')"
    }
}

fun Role.toViewDto(): RoleViewDto = RoleViewDto(
    id = id,
    name = name,
)