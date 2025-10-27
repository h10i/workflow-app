package com.example.workflow.core.workflow.request

import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "request_type")
data class RequestType(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description", nullable = true)
    var description: String?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "schema_definition", columnDefinition = "jsonb", nullable = false)
    val schemaDefinition: JsonNode,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestType

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "RequestType(id=$id, name='$name', description=$description, schemaDefinition=$schemaDefinition)"
    }
}

fun RequestType.toViewDto() = RequestTypeViewDto(
    id = id,
    name = name,
    description = description,
    schemaDefinition = schemaDefinition,
)
