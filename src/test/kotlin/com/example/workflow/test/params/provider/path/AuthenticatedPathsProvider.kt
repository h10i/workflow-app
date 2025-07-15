package com.example.workflow.test.params.provider.path

import com.example.workflow.test.constants.TestTargetPaths
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

class AuthenticatedPathsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
        return TestTargetPaths.authenticatedPaths.stream().map(Arguments::of)
    }
}