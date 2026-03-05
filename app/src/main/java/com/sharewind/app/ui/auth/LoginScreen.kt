package com.sharewind.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sharewind.app.ui.components.ShareWindTextField
import com.sharewind.app.ui.theme.ShareWindTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val phone by viewModel.phone.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.NavigateToHome -> onNavigateToHome()
                is LoginUiEvent.NavigateToRegister -> onNavigateToRegister()
                is LoginUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LoginContent(
            modifier = Modifier.padding(padding),
            uiState = uiState,
            phone = phone,
            password = password,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    phone: String,
    password: String,
    onAction: (LoginUiAction) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo or App Name
            Text(
                text = "ShareWind",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Peer-to-peer ride sharing",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            ShareWindTextField(
                value = phone,
                onValueChange = { onAction(LoginUiAction.OnPhoneChanged(it)) },
                label = "Phone Number",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ShareWindTextField(
                value = password,
                onValueChange = { onAction(LoginUiAction.OnPasswordChanged(it)) },
                label = "Password",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAction(LoginUiAction.OnLoginClicked)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(16.dp),
                enabled = uiState !is LoginUiState.Loading
            ) {
                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { onAction(LoginUiAction.OnRegisterClicked) }) {
                Text(
                    text = "Don't have an account? Register",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (uiState is LoginUiState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    ShareWindTheme {
        LoginContent(
            modifier = Modifier,
            uiState = LoginUiState.Idle,
            phone = "",
            password = "",
            onAction = {}
        )
    }
}
