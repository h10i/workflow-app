package com.example.workflow.common.model

import io.swagger.v3.oas.annotations.media.Schema

data class UnifiedErrorResponse(
    @get:Schema(
        description = "A map containing error details. " +
                "Keys are either specific field names (for validation errors) " +
                "or general error categories (e.g., 'general', 'global'). " +
                "Values are lists of error messages for the corresponding key.",
        example = """
            {
              "field1": [
                "field1 is invalid format."
              ],
              "field2": [
                "field2 must be at least 8 characters long.",
                "field2 must include special characters."
              ],
              "general": [
                "An unexpected error occurred. Please try again later."
              ]
            }
            """
    )
    val errors: Map<String, List<String>>
)