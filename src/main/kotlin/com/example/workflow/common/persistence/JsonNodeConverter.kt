package com.example.workflow.common.persistence

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class JsonNodeConverter : AttributeConverter<JsonNode, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: JsonNode?): String? {
        return attribute?.let {
            try {
                objectMapper.writeValueAsString(it)
            } catch (exception: JsonProcessingException) {
                throw IllegalStateException("Failed to serialize JsonNode to JSON string", exception)
            }
        }
    }

    override fun convertToEntityAttribute(dbData: String?): JsonNode? {
        return dbData?.let {
            try {
                objectMapper.readTree(it)
            } catch (exception: JsonProcessingException) {
                throw IllegalStateException("Failed to deserialize JSON string to JsonNode", exception)
            }
        }
    }
}
