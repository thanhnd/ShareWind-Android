package com.sharewind.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sharewind.app.ui.components.ShareWindTextField
import com.sharewind.app.ui.theme.ShareWindTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val otp by viewModel.otp.collectAsStateWithLifecycle()
    val phone = viewModel.phone
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is OtpUiEvent.NavigateToHome -> onNavigateToHome()
                is OtpUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Verify Phone") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        OtpContent(
            modifier = Modifier.padding(padding),
            uiState = uiState,
            otp = otp,
            phone = phone,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun OtpContent(
    modifier: Modifier = Modifier,
    uiState: OtpUiState,
    otp: String,
    phone: String,
    onAction: (OtpUiAction) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Verification Code",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter the 6-digit code sent to\n$phone",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        ShareWindTextField(
            value = otp,
            onValueChange = { onAction(OtpUiAction.OnOtpChanged(it)) },
            label = "6-Digit Code",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.width(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onAction(OtpUiAction.OnVerifyClicked)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(16.dp),
            enabled = uiState !is OtpUiState.Loading && otp.length == 6
        ) {
            if (uiState is OtpUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Verify & Login", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { onAction(OtpUiAction.OnResendClicked) }) {
            Text(
                text = "Didn't receive code? Resend",
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (uiState is OtpUiState.Error) {
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
fun OtpContentPreview() {
    ShareWindTheme {
        OtpContent(
            uiState = OtpUiState.Idle,
            otp = "",
            phone = "+84 123 456 789",
            onAction = {}
        )
    }
}
