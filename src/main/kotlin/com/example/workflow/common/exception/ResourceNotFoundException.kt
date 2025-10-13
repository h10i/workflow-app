package com.example.workflow.common.exception

open class ResourceNotFoundException(
    val resourceName: String,
    val searchCriteria: Map<String, String> = emptyMap()
) : RuntimeException(buildMessage(resourceName, searchCriteria)) {
    private companion object {
        private fun buildMessage(resourceName: String, searchCriteria: Map<String, String>): String {
            val baseMessage = "$resourceName not found"
            return if (searchCriteria.isNotEmpty()) {
                val criteriaString = searchCriteria.entries.joinToString(", ") { "${it.key}: ${it.value}" }
                "$baseMessage ($criteriaString)"
            } else {
                "$baseMessage."
            }
        }
    }
}
