package com.sharewind.app.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.sharewind.app.ui.theme.ShareWindTheme
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysAllElements() {
        composeTestRule.setContent {
            ShareWindTheme {
                LoginContent(
                    uiState = LoginUiState.Idle,
                    phone = "",
                    password = "",
                    onAction = {}
                )
            }
        }

        composeTestRule.onNodeWithText("ShareWind").assertIsDisplayed()
        composeTestRule.onNodeWithText("Phone Number").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun loginScreen_enteringCredentials_updatesFields() {
        var capturedPhone = ""
        var capturedPassword = ""

        composeTestRule.setContent {
            ShareWindTheme {
                LoginContent(
                    uiState = LoginUiState.Idle,
                    phone = "",
                    password = "",
                    onAction = { action ->
                        when (action) {
                            is LoginUiAction.OnPhoneChanged -> capturedPhone = action.phone
                            is LoginUiAction.OnPasswordChanged -> capturedPassword = action.password
                            else -> {}
                        }
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("Phone Number").performTextInput("0123456789")
        assert(capturedPhone == "0123456789")

        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        assert(capturedPassword == "password123")
    }
}
