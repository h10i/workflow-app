package com.example.workflow.integration.feature.role.controller

import com.example.workflow.common.path.ApiPath
import com.example.workflow.feature.role.controller.RoleController
import com.example.workflow.feature.role.model.CreateRoleRequest
import com.example.workflow.feature.role.model.RoleViewListResponse
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.presenter.CreateRolePresenter
import com.example.workflow.feature.role.presenter.GetAllRolesPresenter
import com.example.workflow.feature.role.usecase.CreateRoleUseCase
import com.example.workflow.feature.role.usecase.GetAllRolesUseCase
import com.example.workflow.integration.test.config.NoSecurityConfig
import com.example.workflow.support.annotation.IntegrationTest
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.assertj.MvcTestResult
import java.util.*
import kotlin.test.assertEquals

@IntegrationTest
@WebMvcTest(RoleController::class)
@Import(RoleControllerApiTest.MockConfig::class, NoSecurityConfig::class)
@Suppress("unused")
class RoleControllerApiTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @Autowired
    private lateinit var createRoleUseCase: CreateRoleUseCase

    @Autowired
    private lateinit var createRolePresenter: CreateRolePresenter

    @Autowired
    private lateinit var getAllRolesUseCase: GetAllRolesUseCase

    @Autowired
    private lateinit var getAllRolesPresenter: GetAllRolesPresenter

    @TestConfiguration
    class MockConfig {
        @Bean
        fun createRoleUseCase(): CreateRoleUseCase = mockk()

        @Bean
        fun createRolePresenter(): CreateRolePresenter = mockk()

        @Bean
        fun getAllRolesUseCase(): GetAllRolesUseCase = mockk()

        @Bean
        fun getAllRolesPresenter(): GetAllRolesPresenter = mockk()
    }

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class CreateRole {
        @Test
        fun `POST v1_roles should return created role information with valid request`() {
            // Arrange
            val roleName = "EXAMPLE"

            val roleId = UUID.randomUUID()
            val roleViewResponse = RoleViewResponse(
                id = roleId,
                name = roleName,
            )

            val useCaseResult: CreateRoleUseCase.Result = mockk()
            val presenterResult: CreateRolePresenter.Result = CreateRolePresenter.Result(
                response = roleViewResponse,
            )

            every { createRoleUseCase.execute(any()) } returns useCaseResult
            every { createRolePresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .post()
                .uri(ApiPath.Role.BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                        "name": "$roleName"
                        }
                    """.trimIndent()
                )
                .exchange()

            // Assert
            val request = slot<CreateRoleRequest>()
            verify(exactly = 1) { createRoleUseCase.execute(capture(request)) }
            val capturedRequest = request.captured
            assertEquals(roleName, capturedRequest.name)

            assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                        {
                        "id": "$roleId",
                        "name": $roleName
                        }
                    """.trimIndent()
                )
        }

        @Test
        fun `POST v1_roles should return created role information with invalid request`() {
            // Arrange
            val roleName = ""

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .post()
                .uri(ApiPath.Role.BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                        "name": "$roleName"
                        }
                    """.trimIndent()
                )
                .exchange()

            // Assert
            assertThat(testResult)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                        {
                            "errors": {
                                "name": [
                                    "Name must not be blank"
                                ]
                            }
                        }
                    """.trimIndent()
                )
        }
    }

    @Nested
    inner class GetAllRoles {
        @Test
        fun `GET v1_roles should return all roles information`() {
            // Arrange
            val useCaseResult: GetAllRolesUseCase.Result = mockk()
            val roleViewResponseList: List<RoleViewResponse> = listOf(
                RoleViewResponse(
                    id = UUID.randomUUID(),
                    name = "EXAMPLE_01",
                ),
                RoleViewResponse(
                    id = UUID.randomUUID(),
                    name = "EXAMPLE_02",
                ),
            )
            val presenterResult = GetAllRolesPresenter.Result(
                response = RoleViewListResponse(
                    roles = roleViewResponseList,
                )
            )

            every { getAllRolesUseCase.execute() } returns useCaseResult
            every { getAllRolesPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val testResult: MvcTestResult = mockMvcTester
                .get()
                .uri(ApiPath.Role.BASE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

            // Assert
            assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .isLenientlyEqualTo(
                    """
                        {
                            "roles": [
                                {
                                    "id": "${roleViewResponseList[0].id}",
                                    "name": "${roleViewResponseList[0].name}"
                                },
                                {
                                    "id": "${roleViewResponseList[1].id}",
                                    "name": "${roleViewResponseList[1].name}"
                                }
                            ]
                        }
                    """.trimIndent()
                )
        }
    }
}