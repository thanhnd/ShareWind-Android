package com.sharewind.app.domain.captcha

import javax.inject.Inject

/**
 * No-op implementation until backend adds CAPTCHA (DEV-77).
 * Returns null — backend accepts requests without captcha_token for now.
 */
class NoOpCaptchaProvider @Inject constructor() : CaptchaProvider {
    override suspend fun getToken(): String? = null
}
