package com.sharewind.app.di

import com.sharewind.app.domain.captcha.CaptchaProvider
import com.sharewind.app.domain.captcha.NoOpCaptchaProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CaptchaModule {

    @Binds
    @Singleton
    abstract fun bindCaptchaProvider(impl: NoOpCaptchaProvider): CaptchaProvider
}
