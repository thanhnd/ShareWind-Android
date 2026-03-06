package com.sharewind.app.ui.auth

import androidx.lifecycle.SavedStateHandle
import com.sharewind.app.data.repository.AuthResult
import com.sharewind.app.data.repository.AuthRepository
import com.sharewind.app.data.remote.RegisterData
import com.sharewind.app.data.remote.TokenData
import com.sharewind.app.data.remote.UserData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: RegisterViewModel
    private lateinit var authRepository: AuthRepository
    private val phone = "1234567890"

    private val fakeRegisterData = RegisterData(
        user = UserData(id = "1", full_name = "John Doe", role = "end_user", status = "active"),
        tokens = TokenData(access_token = "a", refresh_token = "r")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        coEvery { authRepository.register(any(), any(), any()) } returns AuthResult.Success(fakeRegisterData)
        val savedStateHandle = SavedStateHandle(mapOf("phone" to phone))
        viewModel = RegisterViewModel(savedStateHandle, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle and phone is correct`() {
        assertEquals(RegisterUiState.Idle, viewModel.uiState.value)
        assertEquals(phone, viewModel.phone)
    }

    @Test
    fun `onNameChanged updates name state`() {
        val name = "John Doe"
        viewModel.onAction(RegisterUiAction.OnNameChanged(name))
        assertEquals(name, viewModel.name.value)
    }

    @Test
    fun `onPasswordChanged updates password state`() {
        val password = "password123"
        viewModel.onAction(RegisterUiAction.OnPasswordChanged(password))
        assertEquals(password, viewModel.password.value)
    }

    @Test
    fun `onRegisterClicked with valid fields transitions to Success`() = runTest {
        viewModel.onAction(RegisterUiAction.OnNameChanged("John Doe"))
        viewModel.onAction(RegisterUiAction.OnPasswordChanged("password123"))

        viewModel.onAction(RegisterUiAction.OnRegisterClicked)

        advanceUntilIdle()

        assertEquals(RegisterUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `onRegisterClicked with empty name does not call repository`() = runTest {
        viewModel.onAction(RegisterUiAction.OnPasswordChanged("password123"))
        viewModel.onAction(RegisterUiAction.OnRegisterClicked)

        advanceUntilIdle()

        assertEquals(RegisterUiState.Idle, viewModel.uiState.value)
    }
}
