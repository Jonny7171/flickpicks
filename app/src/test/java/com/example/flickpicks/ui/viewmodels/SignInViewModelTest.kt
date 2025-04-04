package com.example.flickpicks.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.flickpicks.data.repository.UserSessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension



@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class SignInViewModelTest {

    @get: Rule

    val instantExecutor = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    @Mock
    lateinit var mockSessionRepository: UserSessionRepository

    private lateinit var viewModel: SignInViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = SignInViewModel(mockSessionRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveSession should call session repository with correct value`() = runTest {

        val userID = "neha123"
        val email = "neha123@email.com"

        viewModel.saveSession(userID, email)

        advanceUntilIdle()

        verify(mockSessionRepository).saveSession(userID, email)

    }



}