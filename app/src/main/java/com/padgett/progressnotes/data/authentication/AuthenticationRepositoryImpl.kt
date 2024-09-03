package com.padgett.progressnotes.data.authentication

import com.padgett.progressnotes.domain.AuthenticationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max

class AuthenticationRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val firebasePhoneAuthenticator: FirebasePhoneAuthenticator
) : AuthenticationRepository {

    private companion object {
        const val ONE_SECOND_IN_MILLIS = 1000
    }

    override fun isSignedIn(): Boolean = firebasePhoneAuthenticator.isSignedIn()

    override suspend fun getSignedInState(): Flow<Boolean> = firebasePhoneAuthenticator.getSignedInState()

    override suspend fun verifyPhoneNumber(phoneNumber: String): Result<Boolean> =
        withContext(ioDispatcher) {
            firebasePhoneAuthenticator.verifyPhoneNumber(phoneNumber)
        }

    override suspend fun verifyCode(code: String): Result<Unit> =
        withContext(ioDispatcher) {
            firebasePhoneAuthenticator.verifyCode(code)
        }

    override fun retryPhoneVerificationTimer(): Flow<Int> =
        flow {
            while (true) {
                val currentTime = System.currentTimeMillis()
                val remainingTimeMillis = ((firebasePhoneAuthenticator.getTimeoutTimeMillis() ?: currentTime) - currentTime)
                emit((max(remainingTimeMillis, 0L) / ONE_SECOND_IN_MILLIS).toInt())
                delay(ONE_SECOND_IN_MILLIS.toLong())
            }
        }

    override fun signOut() {
        firebasePhoneAuthenticator.signOut()
    }

    override suspend fun deleteAccount(): Result<Unit> =
        withContext(ioDispatcher) {
            firebasePhoneAuthenticator.deleteAccount()
        }

    override suspend fun isXeroAuthenticated(): Flow<Boolean> {
        return flowOf(false)
    }
}