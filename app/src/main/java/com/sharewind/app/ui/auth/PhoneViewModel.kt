package com.sharewind.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharewind.app.data.repository.AuthResult
import com.sharewind.app.data.repository.AuthRepository
import com.sharewind.app.domain.captcha.CaptchaProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PhoneUiState {
    object Idle : PhoneUiState()
    object Loading : PhoneUiState()
    data class Error(val message: String) : PhoneUiState()
}

sealed class PhoneUiEvent {
    data class NavigateToOtp(val phone: String) : PhoneUiEvent()
    data class ShowSnackbar(val message: String) : PhoneUiEvent()
}

sealed class PhoneUiAction {
    data class OnPhoneChanged(val phone: String) : PhoneUiAction()
    object OnSendOtpClicked : PhoneUiAction()
}

@HiltViewModel
class PhoneViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val captchaProvider: CaptchaProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhoneUiState>(PhoneUiState.Idle)
    val uiState: StateFlow<PhoneUiState> = _uiState.asStateFlow()

    private val _events = Channel<PhoneUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    fun onAction(action: PhoneUiAction) {
        when (action) {
            is PhoneUiAction.OnPhoneChanged -> _phone.value = action.phone
            is PhoneUiAction.OnSendOtpClicked -> sendOtp()
        }
    }

    private fun sendOtp() {
        val phoneNumber = _phone.value.trim()
        if (phoneNumber.isBlank()) {
            viewModelScope.launch {
                _events.send(PhoneUiEvent.ShowSnackbar("Please enter your phone number"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = PhoneUiState.Loading
            val captchaToken = captchaProvider.getToken()
            when (val result = authRepository.sendOtp(phoneNumber, captchaToken)) {
                is AuthResult.Success -> {
                    _uiState.value = PhoneUiState.Idle
                    _events.send(PhoneUiEvent.NavigateToOtp(phoneNumber))
                }
                is AuthResult.Error -> {
                    _uiState.value = PhoneUiState.Error(result.message)
                }
            }
        }
    }
}
