package com.example.workflow.infra.security.config

import com.example.workflow.infra.security.model.RsaKeyProperties
import com.example.workflow.test.annotation.UnitTest
import com.example.workflow.test.util.TestDataFactory
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@UnitTest
class SecurityConfigTest {
    private lateinit var rsaKeyProperties: RsaKeyProperties
    private lateinit var securityConfig: SecurityConfig

    @BeforeEach
    fun setUp() {
        val keyPair: KeyPair = TestDataFactory.generateTestRsaKeyPair()
        rsaKeyProperties = RsaKeyProperties(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey)
        securityConfig = SecurityConfig(rsaKeyProperties)
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class PasswordEncoderMethod {
        @Test
        fun `passwordEncoder returns BCryptPasswordEncoder`() {
            // Arrange

            // Act
            val encoder = securityConfig.passwordEncoder()

            // Assert
            assertTrue(encoder is BCryptPasswordEncoder)
        }
    }

    @Nested
    inner class JwtDecoder {
        @Test
        fun `jwtDecoder returns NimbusJwtDecoder`() {
            // Arrange

            // Act
            val decoder = securityConfig.jwtDecoder()

            // Assert
            assertNotNull(decoder)
        }
    }

    @Nested
    inner class JwtEncoder() {
        @Test
        fun `jwtEncoder returns NimbusJwtEncoder`() {
            // Arrange

            // Act
            val encoder = securityConfig.jwtEncoder()

            // Assert
            assertNotNull(encoder)
        }
    }

    @Nested
    inner class JwtAuthenticationConverter() {
        @Test
        fun `jwtAuthenticationConverter returns JwtAuthenticationConverter`() {
            // Arrange

            // Act
            val converter = securityConfig.jwtAuthenticationConverter()

            // Assert
            assertNotNull(converter)
        }
    }

    @Nested
    inner class AuthenticationManager() {
        @Test
        fun `authenticationManager returns configured ProviderManager`() {
            // Arrange
            val userDetailsService: UserDetailsService = mockk()
            val passwordEncoder: PasswordEncoder = mockk()

            // Act
            val authManager = securityConfig.authenticationManager(userDetailsService, passwordEncoder)

            // Assert
            assertTrue(authManager is ProviderManager)
        }
    }
}