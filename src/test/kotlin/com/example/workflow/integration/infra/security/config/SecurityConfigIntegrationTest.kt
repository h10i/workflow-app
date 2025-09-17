package com.example.workflow.integration.infra.security.config

import com.example.workflow.infra.security.config.SecurityConfig
import com.example.workflow.integration.test.controller.SecurityConfigTestController
import com.example.workflow.integration.test.path.AuthenticatedPathsProvider
import com.example.workflow.integration.test.path.HasAdminPathsProvider
import com.example.workflow.integration.test.path.PermitAllPathsProvider
import com.example.workflow.support.annotation.IntegrationTest
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.assertj.MvcTestResult

@IntegrationTest
@WebMvcTest(SecurityConfigTestController::class)
@Import(SecurityConfig::class, SecurityConfigIntegrationTest.MockConfig::class)
@ActiveProfiles("test", "security-test-controller")
class SecurityConfigIntegrationTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @TestConfiguration
    @Suppress("unused")
    class MockConfig {
        @Bean
        fun userDetailsService(): UserDetailsService = mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class PermitAllPaths {
        @ParameterizedTest
        @ArgumentsSource(PermitAllPathsProvider::class)
        fun `should return 200 OK without credentials`(method: HttpMethod, path: String) {
            // Arrange

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .method(method)
                .uri(path)
                .exchange()

            // Assert
            Assertions.assertThat(testResult)
                .hasStatusOk()
        }

        @ParameterizedTest
        @ArgumentsSource(PermitAllPathsProvider::class)
        @WithMockUser
        fun `should return 200 OK with credentials`(method: HttpMethod, path: String) {
            // Arrange

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .method(method)
                .uri(path)
                .exchange()

            // Assert
            Assertions.assertThat(testResult)
                .hasStatusOk()
        }
    }

    @Nested
    inner class AuthenticatedPaths {
        @ParameterizedTest
        @ArgumentsSource(AuthenticatedPathsProvider::class)
        fun `should return 401 Unauthorized without credentials`(method: HttpMethod, path: String) {
            // Arrange

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .method(method)
                .uri(path)
                .exchange()

            // Assert
            Assertions.assertThat(testResult)
                .hasStatus(HttpStatus.UNAUTHORIZED)
        }

        @ParameterizedTest
        @ArgumentsSource(AuthenticatedPathsProvider::class)
        @WithMockUser
        fun `should return 200 OK with credentials`(method: HttpMethod, path: String) {
            // Arrange

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .method(method)
                .uri(path)
                .exchange()

            // Assert
            Assertions.assertThat(testResult)
                .hasStatus(HttpStatus.OK)
        }
    }

    @Nested
    inner class HsaAdminPaths {
        @ParameterizedTest
        @ArgumentsSource(HasAdminPathsProvider::class)
        @WithMockUser(roles = ["USER"])
        fun `should return 403 Forbidden with non-ADMIN credentials`(method: HttpMethod, path: String) {
            // Arrange

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .method(method)
                .uri(path)
                .exchange()

            // Assert
            Assertions.assertThat(testResult)
                .hasStatus(HttpStatus.FORBIDDEN)
        }

        @ParameterizedTest
        @ArgumentsSource(HasAdminPathsProvider::class)
        @WithMockUser(roles = ["ADMIN"])
        fun `should return 200 OK with ADMIN credentials`(method: HttpMethod, path: String) {
            // Arrange

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .method(method)
                .uri(path)
                .exchange()

            // Assert
            Assertions.assertThat(testResult)
                .hasStatus(HttpStatus.OK)
        }
    }
}
