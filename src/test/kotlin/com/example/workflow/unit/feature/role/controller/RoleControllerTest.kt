package com.example.workflow.unit.feature.role.controller

import com.example.workflow.feature.role.controller.RoleController
import com.example.workflow.feature.role.model.CreateRoleRequest
import com.example.workflow.feature.role.model.RoleViewListResponse
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.presenter.CreateRolePresenter
import com.example.workflow.feature.role.presenter.GetAllRolesPresenter
import com.example.workflow.feature.role.usecase.CreateRoleUseCase
import com.example.workflow.feature.role.usecase.GetAllRolesUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@UnitTest
class RoleControllerTest {
    private lateinit var createRoleUseCase: CreateRoleUseCase
    private lateinit var createRolePresenter: CreateRolePresenter
    private lateinit var getAllRolesUseCase: GetAllRolesUseCase
    private lateinit var getAllRolesPresenter: GetAllRolesPresenter
    private lateinit var roleController: RoleController

    @BeforeEach
    fun setUp() {
        createRoleUseCase = mockk()
        createRolePresenter = mockk()
        getAllRolesUseCase = mockk()
        getAllRolesPresenter = mockk()
        roleController = RoleController(
            createRoleUseCase = createRoleUseCase,
            createRolePresenter = createRolePresenter,
            getAllRolesUseCase = getAllRolesUseCase,
            getAllRolesPresenter = getAllRolesPresenter,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class CreateRole() {
        @Test
        fun `returns role view response`() {
            // Arrange
            val request = CreateRoleRequest(
                name = "EXAMPLE",
            )
            val useCaseResult: CreateRoleUseCase.Result = mockk()
            val roleViewResponse: RoleViewResponse = mockk()
            val presenterResult: CreateRolePresenter.Result = CreateRolePresenter.Result(
                response = roleViewResponse,
            )

            every { createRoleUseCase.execute(request) } returns useCaseResult
            every { createRolePresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val actual = roleController.createRole(request)

            // Assert
            assertEquals(HttpStatus.CREATED, actual.statusCode)
            assertEquals(roleViewResponse, actual.body)
        }
    }

    @Nested
    inner class GetAllRoles() {
        @Test
        fun `should return role view response list`() {
            // Arrange
            val useCaseResult: GetAllRolesUseCase.Result = mockk()
            val presenterResult: GetAllRolesPresenter.Result = mockk()
            val response: RoleViewListResponse = mockk()

            every { presenterResult.response } returns response
            every { getAllRolesUseCase.execute() } returns useCaseResult
            every { getAllRolesPresenter.toResponse(useCaseResult) } returns presenterResult

            // Act
            val actual = roleController.getAllRoles()

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(response, actual.body)
        }
    }
}