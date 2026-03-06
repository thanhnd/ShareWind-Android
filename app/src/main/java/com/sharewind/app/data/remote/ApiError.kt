package com.sharewind.app.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Error response envelope from ShareWind backend.
 */
data class ApiError(
    val error: ErrorDetail
)

data class ErrorDetail(
    val code: String,
    val message: String
)
