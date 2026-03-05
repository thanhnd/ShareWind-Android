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

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Error(val message: String) : LoginUiState()
    object Success : LoginUiState()
}

sealed class LoginUiEvent {
    object NavigateToRegister : LoginUiEvent()
    object NavigateToHome : LoginUiEvent()
    data class ShowSnackbar(val message: String) : LoginUiEvent()
}

sealed class LoginUiAction {
    data class OnPhoneChanged(val phone: String) : LoginUiAction()
    data class OnPasswordChanged(val password: String) : LoginUiAction()
    object OnLoginClicked : LoginUiAction()
    object OnRegisterClicked : LoginUiAction()
}

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<LoginUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.OnPhoneChanged -> _phone.value = action.phone
            is LoginUiAction.OnPasswordChanged -> _password.value = action.password
            is LoginUiAction.OnLoginClicked -> login()
            is LoginUiAction.OnRegisterClicked -> {
                viewModelScope.launch { _events.send(LoginUiEvent.NavigateToRegister) }
            }
        }
    }

    private fun login() {
        val currentPhone = _phone.value
        val currentPassword = _password.value

        if (currentPhone.isBlank() || currentPassword.isBlank()) {
            viewModelScope.launch { _events.send(LoginUiEvent.ShowSnackbar("Please fill in all fields")) }
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            // TODO: Call LoginUseCase
            // Mocking a successful login for now
            kotlinx.coroutines.delay(1500)
            _uiState.value = LoginUiState.Success
            _events.send(LoginUiEvent.NavigateToHome)
        }
    }
}
