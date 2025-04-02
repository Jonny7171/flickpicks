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
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mock
import org.mockito.Mockito.*
import kotlin.test.assertTrue


@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class SettingsViewModelTest {

    @get: Rule

    val instantExecutor = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    @Mock
    lateinit var mockSessionRepository: UserSessionRepository

    @Mock
    lateinit var mockAuthManager: AuthManager

    private lateinit var viewModel: SettingsViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = SettingsViewModel(mockSessionRepository, mockAuthManager)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `logout should clear session, sign out, and invoke callback`() = runTest {
        var wasCalled = false

        viewModel.logout {
            wasCalled = true
        }

        advanceUntilIdle()

        verify(mockSessionRepository).clearSession()
        verify(mockAuthManager).signOut()
        assertTrue(wasCalled)
    }

}