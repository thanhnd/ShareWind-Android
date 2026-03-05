package com.sharewind.app.ui.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `onPhoneChanged updates phone state`() {
        val phone = "1234567890"
        viewModel.onAction(LoginUiAction.OnPhoneChanged(phone))
        assertEquals(phone, viewModel.phone.value)
    }

    @Test
    fun `onPasswordChanged updates password state`() {
        val password = "password123"
        viewModel.onAction(LoginUiAction.OnPasswordChanged(password))
        assertEquals(password, viewModel.password.value)
    }

    @Test
    fun `onLoginClicked with empty fields does not trigger loading`() = runTest {
        viewModel.onAction(LoginUiAction.OnLoginClicked)
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `onLoginClicked with valid fields transitions to Success`() = runTest {
        viewModel.onAction(LoginUiAction.OnPhoneChanged("1234567890"))
        viewModel.onAction(LoginUiAction.OnPasswordChanged("password"))
        
        viewModel.onAction(LoginUiAction.OnLoginClicked)
        
        // Advance until the delay(1500) is finished
        advanceTimeBy(1600)
        
        assertEquals(LoginUiState.Success, viewModel.uiState.value)
    }
}
