package com.padgett.progressnotes.injection.module

import com.padgett.progressnotes.data.authentication.AuthenticationRepositoryImpl
import com.padgett.progressnotes.domain.AuthenticationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class AuthenticationModule {
    @Binds
    abstract fun bindAuthenticationRepository(authenticationRepositoryImpl: AuthenticationRepositoryImpl): AuthenticationRepository
}