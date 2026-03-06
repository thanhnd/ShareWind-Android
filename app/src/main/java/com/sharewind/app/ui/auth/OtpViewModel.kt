package com.sharewind.app.ui.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharewind.app.data.repository.AuthResult
import com.sharewind.app.data.repository.AuthRepository
import com.sharewind.app.domain.captcha.CaptchaProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

const val OTP_COOLDOWN_SECONDS = 60

sealed class OtpUiState {
    object Idle : OtpUiState()
    object Loading : OtpUiState()
    data class Error(val message: String) : OtpUiState()
    object Success : OtpUiState()
}

sealed class OtpUiEvent {
    object NavigateToRegister : OtpUiEvent()
    data class ShowSnackbar(val message: String) : OtpUiEvent()
}

sealed class OtpUiAction {
    data class OnOtpChanged(val otp: String) : OtpUiAction()
    object OnVerifyClicked : OtpUiAction()
    object OnResendClicked : OtpUiAction()
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val captchaProvider: CaptchaProvider
) : ViewModel() {

    val phone: String = checkNotNull(savedStateHandle["phone"])

    private val _uiState = MutableStateFlow<OtpUiState>(OtpUiState.Idle)
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private val _events = Channel<OtpUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    private val _resendCooldownSeconds = MutableStateFlow(0)
    val resendCooldownSeconds: StateFlow<Int> = _resendCooldownSeconds.asStateFlow()

    private var cooldownJob: Job? = null

    init {
        // User just received OTP from PhoneScreen; start cooldown so Resend is disabled for 60s
        startCooldown()
    }

    fun onAction(action: OtpUiAction) {
        when (action) {
            is OtpUiAction.OnOtpChanged -> {
                if (action.otp.length <= 6 && action.otp.all { it.isDigit() }) {
                    _otp.value = action.otp
                }
            }
            is OtpUiAction.OnVerifyClicked -> verifyOtp()
            is OtpUiAction.OnResendClicked -> resendOtp()
        }
    }

    private fun verifyOtp() {
        if (_otp.value.length < 6) {
            viewModelScope.launch {
                _events.send(OtpUiEvent.ShowSnackbar("Please enter the 6-digit code"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = OtpUiState.Loading
            when (val result = authRepository.verifyOtp(phone, _otp.value)) {
                is AuthResult.Success -> {
                    _uiState.value = OtpUiState.Success
                    _events.send(OtpUiEvent.NavigateToRegister)
                }
                is AuthResult.Error -> {
                    _uiState.value = OtpUiState.Error(result.message)
                }
            }
        }
    }

    private fun resendOtp() {
        if (_resendCooldownSeconds.value > 0) return

        viewModelScope.launch {
            _uiState.value = OtpUiState.Loading
            val captchaToken = captchaProvider.getToken()
            when (val result = authRepository.sendOtp(phone, captchaToken)) {
                is AuthResult.Success -> {
                    _uiState.value = OtpUiState.Idle
                    startCooldown()
                    _events.send(OtpUiEvent.ShowSnackbar("OTP resent to $phone"))
                }
                is AuthResult.Error -> {
                    _uiState.value = OtpUiState.Error(result.message)
                }
            }
        }
    }

    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (remaining in OTP_COOLDOWN_SECONDS downTo 1) {
                if (!isActive) break
                _resendCooldownSeconds.value = remaining
                delay(1000)
            }
            _resendCooldownSeconds.value = 0
        }
    }

    override fun onCleared() {
        super.onCleared()
        cooldownJob?.cancel()
    }
}
