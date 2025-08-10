package com.example.workflow.integration.test.path

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.springframework.http.HttpMethod
import java.util.stream.Stream

class HasAdminPathsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? = Stream.of(
        // Role
        Arguments.of(HttpMethod.POST, "/v1/roles"),
    )
}