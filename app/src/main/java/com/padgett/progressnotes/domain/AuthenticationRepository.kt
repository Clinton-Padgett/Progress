package com.padgett.progressnotes.domain

import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    fun isSignedIn(): Boolean
    suspend fun getSignedInState(): Flow<Boolean>

    suspend fun verifyPhoneNumber(phoneNumber: String): Result<Boolean>
    suspend fun verifyCode(code: String): Result<Unit>

    fun retryPhoneVerificationTimer(): Flow<Int>

    fun signOut()
    suspend fun deleteAccount(): Result<Unit>
}