package com.sharewind.app.navigation

sealed class Screen(val route: String) {
    object Login : Screen("auth/login")
    object Register : Screen("auth/register")
    object Otp : Screen("auth/otp/{phone}") {
        fun createRoute(phone: String) = "auth/otp/$phone"
    }
    
    object EndUserHome : Screen("home/eu")
    object DriverHome : Screen("home/driver")
    
    object CreateRequest : Screen("ride/create-request")
    object CreateRide : Screen("ride/create-ride")
    object RideDetail : Screen("ride/{id}") {
        fun createRoute(id: String) = "ride/$id"
    }
    
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
}
