package com.sharewind.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/otp/send")
    suspend fun sendOtp(@Body body: SendOtpRequest): Response<Unit>

    @POST("api/v1/auth/otp/verify")
    suspend fun verifyOtp(@Body body: VerifyOtpRequest): Response<Unit>

    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>
}

data class SendOtpRequest(
    val identifier: String,
    val captcha_token: String? = null
)

data class VerifyOtpRequest(
    val identifier: String,
    val code: String
)

data class RegisterRequest(
    val full_name: String,
    val phone: String,
    val password: String,
    val role: String = "end_user"
)

data class RegisterResponse(
    val data: RegisterData
)

data class RegisterData(
    val user: UserData,
    val tokens: TokenData
)

data class UserData(
    val id: String,
    val full_name: String,
    val role: String,
    val status: String
)

data class TokenData(
    val access_token: String,
    val refresh_token: String
)
