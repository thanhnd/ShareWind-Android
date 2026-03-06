package com.sharewind.app.ui.auth

import androidx.lifecycle.SavedStateHandle
import com.sharewind.app.data.repository.AuthResult
import com.sharewind.app.data.repository.AuthRepository
import com.sharewind.app.domain.captcha.CaptchaProvider
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
class OtpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: OtpViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var captchaProvider: CaptchaProvider
    private val phone = "1234567890"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        captchaProvider = mockk()
        coEvery { captchaProvider.getToken() } returns null
        val savedStateHandle = SavedStateHandle(mapOf("phone" to phone))
        viewModel = OtpViewModel(savedStateHandle, authRepository, captchaProvider)
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
        assertEquals("123", viewModel.otp.value) // Stays at 6 chars max
    }

    @Test
    fun `onVerifyClicked with valid otp transitions to Success`() = runTest {
        coEvery { authRepository.verifyOtp(phone, "123456") } returns AuthResult.Success(Unit)

        viewModel.onAction(OtpUiAction.OnOtpChanged("123456"))
        viewModel.onAction(OtpUiAction.OnVerifyClicked)

        advanceUntilIdle()

        assertEquals(OtpUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `onVerifyClicked with invalid otp shows Error`() = runTest {
        coEvery { authRepository.verifyOtp(phone, "123456") } returns AuthResult.Error(null, "Invalid code")

        viewModel.onAction(OtpUiAction.OnOtpChanged("123456"))
        viewModel.onAction(OtpUiAction.OnVerifyClicked)

        advanceUntilIdle()

        assertEquals(OtpUiState.Error("Invalid code"), viewModel.uiState.value)
    }
}
