package com.example.workflow.unit.feature.workflow.presenter.request

import com.example.workflow.feature.workflow.model.request.RequestTypeViewDto
import com.example.workflow.feature.workflow.model.request.RequestTypeViewResponse
import com.example.workflow.feature.workflow.model.request.toViewResponse
import com.example.workflow.feature.workflow.presenter.request.RequestTypePresenter
import com.example.workflow.support.annotation.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@UnitTest
class RequestTypePresenterTest {
    private lateinit var requestTypePresenter: RequestTypePresenter

    @BeforeEach
    fun setUp() {
        requestTypePresenter = RequestTypePresenter()
    }

    @Nested
    inner class ToResponseFun {
        @BeforeEach
        fun setUp() {
            mockkStatic(RequestTypeViewDto::toViewResponse)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(RequestTypeViewDto::toViewResponse)
        }

        @Test
        fun `should return a presenter result`() {
            // Arrange
            val requestTypeViewDto: RequestTypeViewDto = mockk()
            val requestTypeViewResponse: RequestTypeViewResponse = mockk()

            every { requestTypeViewDto.toViewResponse() } returns requestTypeViewResponse

            // Act
            val actual = requestTypePresenter.toResponse(requestTypeViewDto)

            // Assert
            assertEquals(requestTypeViewResponse, actual.response)
        }
    }

    @Nested
    inner class ToResponseFunWithList {
        @BeforeEach
        fun setUp() {
            mockkStatic(RequestTypeViewDto::toViewResponse)
        }

        @AfterEach
        fun tearDown() {
            unmockkStatic(RequestTypeViewDto::toViewResponse)
        }

        @Test
        fun `should return a presenter result`() {
            // Arrange
            val requestTypeViewDtoList: List<RequestTypeViewDto> = listOf(mockk(), mockk())
            val requestTypeViewResponseList: List<RequestTypeViewResponse> = listOf(mockk(), mockk())

            for (i in 0 until 2) {
                every { requestTypeViewDtoList[i].toViewResponse() } returns requestTypeViewResponseList[i]
            }

            // Act
            val actual = requestTypePresenter.toResponse(requestTypeViewDtoList)

            // Assert
            for (i in 0 until 2) {
                assertEquals(requestTypeViewResponseList[i], actual.response.requestTypes[i])
            }
        }
    }
}
