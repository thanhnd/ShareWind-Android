package com.sharewind.app.ui.auth

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OtpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: OtpViewModel
    private val phone = "1234567890"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val savedStateHandle = SavedStateHandle(mapOf("phone" to phone))
        viewModel = OtpViewModel(savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle and phone is correct`() {
        assertEquals(OtpUiState.Idle, viewModel.uiState.value)
        assertEquals(phone, viewModel.phone)
    }

    @Test
    fun `onOtpChanged updates otp state up to 6 characters`() {
        viewModel.onAction(OtpUiAction.OnOtpChanged("123"))
        assertEquals("123", viewModel.otp.value)
        
        viewModel.onAction(OtpUiAction.OnOtpChanged("1234567"))
        assertEquals("123", viewModel.otp.value) // Should not update if > 6
    }

    @Test
    fun `onVerifyClicked with valid otp transitions to Success`() = runTest {
        viewModel.onAction(OtpUiAction.OnOtpChanged("123456"))
        viewModel.onAction(OtpUiAction.OnVerifyClicked)
        
        advanceTimeBy(1600)
        
        assertEquals(OtpUiState.Success, viewModel.uiState.value)
    }
}
