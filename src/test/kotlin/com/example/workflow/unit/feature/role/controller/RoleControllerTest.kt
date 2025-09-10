package com.example.workflow.unit.feature.role.controller

import com.example.workflow.feature.role.controller.RoleController
import com.example.workflow.feature.role.model.CreateRoleRequest
import com.example.workflow.feature.role.model.RoleViewListResponse
import com.example.workflow.feature.role.model.RoleViewResponse
import com.example.workflow.feature.role.presenter.RolePresenter
import com.example.workflow.feature.role.usecase.CreateRoleUseCase
import com.example.workflow.feature.role.usecase.DeleteRoleUseCase
import com.example.workflow.feature.role.usecase.GetAllRolesUseCase
import com.example.workflow.feature.role.usecase.GetRoleUseCase
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class RoleControllerTest {
    private lateinit var rolePresenter: RolePresenter
    private lateinit var createRoleUseCase: CreateRoleUseCase
    private lateinit var getRoleUseCase: GetRoleUseCase
    private lateinit var getAllRolesUseCase: GetAllRolesUseCase
    private lateinit var deleteRoleUseCase: DeleteRoleUseCase
    private lateinit var roleController: RoleController

    @BeforeEach
    fun setUp() {
        rolePresenter = mockk()
        createRoleUseCase = mockk()
        getRoleUseCase = mockk()
        getAllRolesUseCase = mockk()
        deleteRoleUseCase = mockk()
        roleController = RoleController(
            rolePresenter = rolePresenter,
            createRoleUseCase = createRoleUseCase,
            getRoleUseCase = getRoleUseCase,
            getAllRolesUseCase = getAllRolesUseCase,
            deleteRoleUseCase = deleteRoleUseCase,
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class CreateRoleFun() {
        @Test
        fun `should execute CreateRoleUseCase and return role view response`() {
            // Arrange
            val request: CreateRoleRequest = mockk()
            val useCaseResult: CreateRoleUseCase.Result = mockk(relaxed = true)
            val roleViewResponse: RoleViewResponse = mockk()
            val presenterResult = RolePresenter.Result(
                response = roleViewResponse,
            )

            every { createRoleUseCase.execute(request) } returns useCaseResult
            every { rolePresenter.toResponse(useCaseResult.roleViewDto) } returns presenterResult

            // Act
            val actual = roleController.createRole(request)

            // Assert
            assertEquals(HttpStatus.CREATED, actual.statusCode)
            assertEquals(roleViewResponse, actual.body)
        }
    }

    @Nested
    inner class GetRoleFun() {
        @Test
        fun `should execute GetRoleUseCase and return role view response`() {
            // Arrange
            val roleId = UUID.randomUUID()
            val useCaseResult: GetRoleUseCase.Result = mockk(relaxed = true)
            val presenterResult: RolePresenter.Result<RoleViewResponse> = mockk()
            val response: RoleViewResponse = mockk()

            every { getRoleUseCase.execute(roleId) } returns useCaseResult
            every { rolePresenter.toResponse(useCaseResult.roleViewDto) } returns presenterResult
            every { presenterResult.response } returns response

            // Act
            val actual = roleController.getRole(roleId)

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(response, actual.body)
        }
    }

    @Nested
    inner class GetAllRolesFun() {
        @Test
        fun `should execute GetAllRolesUseCase and return role view response list`() {
            // Arrange
            val useCaseResult: GetAllRolesUseCase.Result = mockk()
            val presenterResult: RolePresenter.Result<RoleViewListResponse> = mockk()
            val response: RoleViewListResponse = mockk()

            every { presenterResult.response } returns response
            every { getAllRolesUseCase.execute() } returns useCaseResult
            every { rolePresenter.toResponse(useCaseResult.roleViewDtoList) } returns presenterResult

            // Act
            val actual = roleController.getAllRoles()

            // Assert
            assertEquals(HttpStatus.OK, actual.statusCode)
            assertEquals(response, actual.body)
        }
    }

    @Nested
    inner class DeleteRoleFun() {
        @Test
        fun `should execute DeleteRoleUseCase and return no content`() {
            // Arrange
            val roleId = UUID.randomUUID()
            every { deleteRoleUseCase.execute(roleId) } just runs

            // Act
            val actual = roleController.deleteRole(roleId)

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
            assertNull(actual.body)
        }
    }
}