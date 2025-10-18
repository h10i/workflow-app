package com.example.workflow.unit.common.persistence

import com.example.workflow.common.persistence.JsonNodeConverter
import com.example.workflow.support.annotation.UnitTest
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@UnitTest
class JsonNodeConverterTest {
    private lateinit var jsonNodeConverter: JsonNodeConverter
    private val objectMapper = spyk(jacksonObjectMapper())

    @BeforeEach
    fun setup() {
        jsonNodeConverter = JsonNodeConverter()
        val field = JsonNodeConverter::class.java.getDeclaredField("objectMapper")
        field.isAccessible = true
        field.set(jsonNodeConverter, objectMapper)
    }

    @Nested
    inner class ConvertToDatabaseColumn {
        @Test
        fun `should return JSON string when valid JsonNode`() {
            // Arrange
            val jsonString = """
                {"name":"Alice"}
            """.trimIndent()
            val jsonNode: JsonNode = objectMapper.readTree(jsonString)

            // Act
            val actual = jsonNodeConverter.convertToDatabaseColumn(jsonNode)

            // Assert
            assertEquals(jsonString, actual)
        }

        @Test
        fun `should return null when attribute is null`() {
            // Arrange

            // Act
            val actual = jsonNodeConverter.convertToDatabaseColumn(null)

            // Assert
            assertNull(actual)
        }

        @Test
        fun `should throw IllegalStateException when serialization fails`() {
            // Arrange
            val jsonNode = objectMapper.readTree("""{"name":"Bob"}""")

            every { objectMapper.writeValueAsString(any()) } throws JsonParseException("fail")

            // Act
            val exception = assertThrows<IllegalStateException> {
                jsonNodeConverter.convertToDatabaseColumn(jsonNode)
            }

            // Assert
            assertEquals("Failed to serialize JsonNode to JSON string", exception.message)
            assertTrue(exception.cause is JsonProcessingException)
        }
    }

    @Nested
    inner class ConvertToEntityAttribute {
        @Test
        fun `should return JsonNode when valid JSON string`() {
            // Arrange
            val key = "age"
            val value = 25
            val jsonString = """{"$key":$value}"""

            // Act
            val actual = jsonNodeConverter.convertToEntityAttribute(jsonString)

            // Assert
            assertEquals(value, actual?.get(key)?.asInt())
        }

        @Test
        fun `should return null when dbData is null`() {
            // Arrange

            // Act
            val actual = jsonNodeConverter.convertToEntityAttribute(null)

            // Assert
            assertNull(actual)
        }

        @Test
        fun `should throw IllegalStateException when deserialization fails`() {
            // Arrange
            val jsonString = """{"invalid"}"""

            every { objectMapper.readTree(jsonString) } throws JsonParseException("invalid")

            // Act
            val exception = assertThrows<IllegalStateException> {
                jsonNodeConverter.convertToEntityAttribute(jsonString)
            }

            // Assert
            assertEquals("Failed to deserialize JSON string to JsonNode", exception.message)
            assertTrue(exception.cause is JsonProcessingException)
        }
    }
}
