package com.padgett.progressnotes.injection.module

import com.padgett.progressnotes.data.clients.ClientRepositoryImpl
import com.padgett.progressnotes.domain.ClientRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ClientModule {
    @Binds
    abstract fun bindClientRepository(clientRepositoryImpl: ClientRepositoryImpl): ClientRepository
}