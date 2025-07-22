package com.example.workflow.integration.test.path

import com.example.workflow.integration.test.constants.TestTargetPaths
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

class PermitAllPathsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
        return TestTargetPaths.permitAllPaths.stream().map(Arguments::of)
    }
}