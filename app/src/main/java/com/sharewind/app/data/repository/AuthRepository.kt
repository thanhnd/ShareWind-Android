package com.sharewind.app.data.repository

import com.sharewind.app.data.remote.ApiError
import com.sharewind.app.data.remote.AuthApi
import com.sharewind.app.data.remote.RegisterData
import com.sharewind.app.data.remote.RegisterRequest
import com.sharewind.app.data.remote.SendOtpRequest
import com.sharewind.app.data.remote.VerifyOtpRequest
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val code: String?, val message: String) : AuthResult<Nothing>()
}

class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val gson: Gson
) {

    suspend fun sendOtp(phone: String, captchaToken: String? = null): AuthResult<Unit> {
        return execute {
            api.sendOtp(SendOtpRequest(identifier = phone, captcha_token = captchaToken))
        }
    }

    suspend fun verifyOtp(phone: String, code: String): AuthResult<Unit> {
        return execute {
            api.verifyOtp(VerifyOtpRequest(identifier = phone, code = code))
        }
    }

    suspend fun register(fullName: String, phone: String, password: String): AuthResult<RegisterData> {
        return try {
            val response = api.register(
                RegisterRequest(full_name = fullName, phone = phone, password = password)
            )
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    AuthResult.Success(data)
                } else {
                    AuthResult.Error(null, "Invalid response")
                }
            } else {
                @Suppress("UNCHECKED_CAST")
                parseError(response) as AuthResult<RegisterData>
            }
        } catch (e: retrofit2.HttpException) {
            @Suppress("UNCHECKED_CAST")
            parseError(e.response()) as AuthResult<RegisterData>
        } catch (e: IOException) {
            AuthResult.Error(null, "Network error. Please check your connection.")
        }
    }

    private suspend fun <T> execute(block: suspend () -> Response<T>): AuthResult<T> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                @Suppress("UNCHECKED_CAST")
                val body = response.body() ?: Unit as T
                AuthResult.Success(body)
            } else {
                @Suppress("UNCHECKED_CAST")
                parseError(response) as AuthResult<T>
            }
        } catch (e: HttpException) {
            @Suppress("UNCHECKED_CAST")
            parseError(e.response()) as AuthResult<T>
        } catch (e: IOException) {
            AuthResult.Error(null, "Network error. Please check your connection.")
        }
    }

    private fun <T> parseError(response: Response<T>?): AuthResult<T> {
        if (response == null) {
            return AuthResult.Error(null, "Unknown error")
        }
        val errorBody = response.errorBody()?.string()
        val code = when (response.code()) {
            429 -> errorBody?.let { parseErrorCode(it) } ?: "RATE_LIMITED"
            else -> errorBody?.let { parseErrorCode(it) }
        }
        val message = errorBody?.let { parseErrorMessage(it) }
            ?: when (code) {
                "OTP_COOLDOWN" -> "Please wait 60 seconds before requesting another code."
                "OTP_TOO_MANY_SENDS" -> "Too many attempts. Please try again in 15 minutes."
                "RATE_LIMITED" -> "Too many requests. Please wait a moment and try again."
                else -> "Something went wrong. Please try again."
            }
        return AuthResult.Error(code, message)
    }

    private fun parseErrorCode(json: String): String? {
        return try {
            gson.fromJson(json, ApiError::class.java).error.code
        } catch (_: Exception) {
            null
        }
    }

    private fun parseErrorMessage(json: String): String? {
        return try {
            gson.fromJson(json, ApiError::class.java).error.message
        } catch (_: Exception) {
            null
        }
    }
}
