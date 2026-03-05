package com.sharewind.app.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.sharewind.app.ui.theme.ShareWindTheme
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_displaysAllElements() {
        composeTestRule.setContent {
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

        composeTestRule.onNodeWithText("Join ShareWind").assertIsDisplayed()
        composeTestRule.onNodeWithText("Full Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Phone Number").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Register").assertIsDisplayed()
    }

    @Test
    fun registerScreen_enteringDetails_updatesFields() {
        var capturedName = ""
        var capturedPhone = ""

        composeTestRule.setContent {
            ShareWindTheme {
                RegisterContent(
                    uiState = RegisterUiState.Idle,
                    name = "",
                    phone = "",
                    password = "",
                    onAction = { action ->
                        when (action) {
                            is RegisterUiAction.OnNameChanged -> capturedName = action.name
                            is RegisterUiAction.OnPhoneChanged -> capturedPhone = action.phone
                            else -> {}
                        }
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("Full Name").performTextInput("John Doe")
        assert(capturedName == "John Doe")

        composeTestRule.onNodeWithText("Phone Number").performTextInput("0123456789")
        assert(capturedPhone == "0123456789")
    }
}
