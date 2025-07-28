package com.example.workflow.integration.test.path

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.springframework.http.HttpMethod
import java.util.stream.Stream

class PermitAllPathsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? = Stream.of(
        // SpringDoc
        Arguments.of(HttpMethod.GET, "/v3/api-docs/**"),
        Arguments.of(HttpMethod.GET, "/swagger-ui.html"),
        Arguments.of(HttpMethod.GET, "/swagger-ui/**"),
        // Auth
        Arguments.of(HttpMethod.POST, "/v1/auth/token"),
        Arguments.of(HttpMethod.POST, "/v1/auth/refresh-token"),
    )
}
