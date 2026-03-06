package com.sharewind.app.domain.captcha

/**
 * Provides CAPTCHA tokens for OTP send requests.
 * When backend adds CAPTCHA verification (DEV-77), implement with Play Integrity API or reCAPTCHA.
 */
interface CaptchaProvider {
    /**
     * Returns a CAPTCHA token, or null if not available/supported.
     * Backend will accept null until CAPTCHA is enforced.
     */
    suspend fun getToken(): String?
}
