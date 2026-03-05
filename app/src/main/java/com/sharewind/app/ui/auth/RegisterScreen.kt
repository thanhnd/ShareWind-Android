package com.sharewind.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun RegisterScreen(
    onNavigateToOtp: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val phone by viewModel.phone.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RegisterUiEvent.NavigateToOtp -> onNavigateToOtp(event.phone)
                is RegisterUiEvent.NavigateToLogin -> onNavigateBack()
                is RegisterUiEvent.ShowSnackbar -> {
                    // TODO: Show snackbar
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        RegisterContent(
            modifier = Modifier.padding(padding),
            uiState = uiState,
            name = name,
            phone = phone,
            password = password,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    uiState: RegisterUiState,
    name: String,
    phone: String,
    password: String,
    onAction: (RegisterUiAction) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Join ShareWind",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter your details to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ShareWindTextField(
            value = name,
            onValueChange = { onAction(RegisterUiAction.OnNameChanged(it)) },
            label = "Full Name",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ShareWindTextField(
            value = phone,
            onValueChange = { onAction(RegisterUiAction.OnPhoneChanged(it)) },
            label = "Phone Number",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ShareWindTextField(
            value = password,
            onValueChange = { onAction(RegisterUiAction.OnPasswordChanged(it)) },
            label = "Password",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onAction(RegisterUiAction.OnRegisterClicked) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(16.dp),
            enabled = uiState !is RegisterUiState.Loading
        ) {
            if (uiState is RegisterUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Register", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { onAction(RegisterUiAction.OnLoginClicked) }) {
            Text(
                text = "Already have an account? Login",
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        if (uiState is RegisterUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterContentPreview() {
    ShareWindTheme {
        RegisterContent(
            uiState = RegisterUiState.Idle,
            name = "",
            phone = "",
            password = "",
            onAction = {}
        )
    }
}
