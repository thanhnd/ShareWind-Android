package com.sharewind.app.ui.auth

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

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
    object Success : RegisterUiState()
}

sealed class RegisterUiEvent {
    data class NavigateToOtp(val phone: String) : RegisterUiEvent()
    object NavigateToLogin : RegisterUiEvent()
    data class ShowSnackbar(val message: String) : RegisterUiEvent()
}

sealed class RegisterUiAction {
    data class OnNameChanged(val name: String) : RegisterUiAction()
    data class OnPhoneChanged(val phone: String) : RegisterUiAction()
    data class OnPasswordChanged(val password: String) : RegisterUiAction()
    object OnRegisterClicked : RegisterUiAction()
    object OnLoginClicked : RegisterUiAction()
}

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = Channel<RegisterUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onAction(action: RegisterUiAction) {
        when (action) {
            is RegisterUiAction.OnNameChanged -> _name.value = action.name
            is RegisterUiAction.OnPhoneChanged -> _phone.value = action.phone
            is RegisterUiAction.OnPasswordChanged -> _password.value = action.password
            is RegisterUiAction.OnRegisterClicked -> register()
            is RegisterUiAction.OnLoginClicked -> {
                viewModelScope.launch { _events.send(RegisterUiEvent.NavigateToLogin) }
            }
        }
    }

    private fun register() {
        if (_name.value.isBlank() || _phone.value.isBlank() || _password.value.isBlank()) {
            viewModelScope.launch { _events.send(RegisterUiEvent.ShowSnackbar("Please fill in all fields")) }
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            // Mocking registration process
            kotlinx.coroutines.delay(1500)
            _uiState.value = RegisterUiState.Success
            _events.send(RegisterUiEvent.NavigateToOtp(_phone.value))
        }
    }
}
