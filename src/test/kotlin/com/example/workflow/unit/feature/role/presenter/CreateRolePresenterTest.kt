package com.example.workflow.unit.feature.role.presenter

import com.example.workflow.feature.role.model.RoleViewDto
import com.example.workflow.feature.role.model.toViewResponse
import com.example.workflow.feature.role.presenter.CreateRolePresenter
import com.example.workflow.feature.role.usecase.CreateRoleUseCase
import com.example.workflow.support.annotation.UnitTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@UnitTest
class CreateRolePresenterTest {
    private lateinit var createRolePresenter: CreateRolePresenter

    @BeforeEach
    fun setUp() {
        createRolePresenter = CreateRolePresenter()
    }

    @AfterEach
    fun tearDown() {
    }

    @Nested
    inner class ToResponse {
        @Test
        fun `toResponse returns presenter result`() {
            // Arrange
            val id = UUID.randomUUID()
            val name = "EXAMPLE"
            val roleViewDto = RoleViewDto(
                id = id,
                name = name,
            )
            val expectedResponse = roleViewDto.toViewResponse()
            val useCaseResult = CreateRoleUseCase.Result(
                roleViewDto = roleViewDto,
            )

            // Act
            val actual: CreateRolePresenter.Result = createRolePresenter.toResponse(useCaseResult)

            // Assert
            assertEquals(expectedResponse, actual.response)
        }
    }
}