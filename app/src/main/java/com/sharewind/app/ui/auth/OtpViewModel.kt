package com.sharewind.app.ui.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OtpUiState {
    object Idle : OtpUiState()
    object Loading : OtpUiState()
    data class Error(val message: String) : OtpUiState()
    object Success : OtpUiState()
}

sealed class OtpUiEvent {
    object NavigateToHome : OtpUiEvent()
    data class ShowSnackbar(val message: String) : OtpUiEvent()
}

sealed class OtpUiAction {
    data class OnOtpChanged(val otp: String) : OtpUiAction()
    object OnVerifyClicked : OtpUiAction()
    object OnResendClicked : OtpUiAction()
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val phone: String = checkNotNull(savedStateHandle["phone"])

    private val _uiState = MutableStateFlow<OtpUiState>(OtpUiState.Idle)
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private val _events = Channel<OtpUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    fun onAction(action: OtpUiAction) {
        when (action) {
            is OtpUiAction.OnOtpChanged -> {
                if (action.otp.length <= 6) {
                    _otp.value = action.otp
                }
            }
            is OtpUiAction.OnVerifyClicked -> verifyOtp()
            is OtpUiAction.OnResendClicked -> resendOtp()
        }
    }

    private fun verifyOtp() {
        if (_otp.value.length < 6) {
            viewModelScope.launch { _events.send(OtpUiEvent.ShowSnackbar("Please enter the 6-digit code")) }
            return
        }

        viewModelScope.launch {
            _uiState.value = OtpUiState.Loading
            // Mocking OTP verification
            kotlinx.coroutines.delay(1500)
            _uiState.value = OtpUiState.Success
            _events.send(OtpUiEvent.NavigateToHome)
        }
    }

    private fun resendOtp() {
        viewModelScope.launch {
            _events.send(OtpUiEvent.ShowSnackbar("OTP resent to $phone"))
        }
    }
}
