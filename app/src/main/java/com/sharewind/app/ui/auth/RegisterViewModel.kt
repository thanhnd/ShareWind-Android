package com.sharewind.app.ui.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharewind.app.data.repository.AuthResult
import com.sharewind.app.data.repository.AuthRepository
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
    object NavigateToHome : RegisterUiEvent()
    object NavigateToLogin : RegisterUiEvent()
    data class ShowSnackbar(val message: String) : RegisterUiEvent()
}

sealed class RegisterUiAction {
    data class OnNameChanged(val name: String) : RegisterUiAction()
    data class OnPasswordChanged(val password: String) : RegisterUiAction()
    object OnRegisterClicked : RegisterUiAction()
    object OnLoginClicked : RegisterUiAction()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {

    val phone: String = checkNotNull(savedStateHandle["phone"])

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = Channel<RegisterUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onAction(action: RegisterUiAction) {
        when (action) {
            is RegisterUiAction.OnNameChanged -> _name.value = action.name
            is RegisterUiAction.OnPasswordChanged -> _password.value = action.password
            is RegisterUiAction.OnRegisterClicked -> register()
            is RegisterUiAction.OnLoginClicked -> {
                viewModelScope.launch { _events.send(RegisterUiEvent.NavigateToLogin) }
            }
        }
    }

    private fun register() {
        val fullName = _name.value.trim()
        val passwordValue = _password.value

        if (fullName.isBlank() || passwordValue.isBlank()) {
            viewModelScope.launch {
                _events.send(RegisterUiEvent.ShowSnackbar("Please fill in all fields"))
            }
            return
        }

        if (passwordValue.length < 8) {
            viewModelScope.launch {
                _events.send(RegisterUiEvent.ShowSnackbar("Password must be at least 8 characters"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            when (val result = authRepository.register(fullName, phone, passwordValue)) {
                is AuthResult.Success -> {
                    _uiState.value = RegisterUiState.Success
                    // TODO: Save tokens to DataStore
                    _events.send(RegisterUiEvent.NavigateToHome)
                }
                is AuthResult.Error -> {
                    _uiState.value = RegisterUiState.Error(result.message)
                }
            }
        }
    }
}
