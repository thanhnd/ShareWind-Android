package com.sharewind.app.ui.auth

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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(RegisterUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `onNameChanged updates name state`() {
        val name = "John Doe"
        viewModel.onAction(RegisterUiAction.OnNameChanged(name))
        assertEquals(name, viewModel.name.value)
    }

    @Test
    fun `onRegisterClicked with valid fields transitions to Success`() = runTest {
        viewModel.onAction(RegisterUiAction.OnNameChanged("John Doe"))
        viewModel.onAction(RegisterUiAction.OnPhoneChanged("1234567890"))
        viewModel.onAction(RegisterUiAction.OnPasswordChanged("password"))
        
        viewModel.onAction(RegisterUiAction.OnRegisterClicked)
        
        advanceTimeBy(1600)
        
        assertEquals(RegisterUiState.Success, viewModel.uiState.value)
    }
}
