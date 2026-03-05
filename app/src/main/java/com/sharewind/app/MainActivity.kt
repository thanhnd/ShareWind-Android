package com.sharewind.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sharewind.app.navigation.Screen
import com.sharewind.app.ui.auth.LoginScreen
import com.sharewind.app.ui.auth.OtpScreen
import com.sharewind.app.ui.auth.RegisterScreen
import com.sharewind.app.ui.theme.ShareWindTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShareWindTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShareWindApp()
                }
            }
        }
    }
}

@Composable
fun ShareWindApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    // TODO: Navigate to Home
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToOtp = { phone ->
                    navController.navigate(Screen.Otp.createRoute(phone))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.Otp.route,
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) {
            OtpScreen(
                onNavigateToHome = {
                    // TODO: Navigate to Home
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
