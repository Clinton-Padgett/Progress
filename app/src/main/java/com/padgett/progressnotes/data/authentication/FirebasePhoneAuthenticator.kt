package com.padgett.progressnotes.data.authentication

import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.padgett.progressnotes.domain.common.exceptions.GenericException
import com.padgett.progressnotes.domain.common.exceptions.InvalidNumberException
import com.padgett.progressnotes.domain.common.exceptions.ProgressException
import com.padgett.progressnotes.domain.common.exceptions.TooManyRequestsException
import com.padgett.progressnotes.injection.ActivityProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebasePhoneAuthenticator @Inject constructor(private val activityProvider: ActivityProvider) {

    private companion object {
        const val TIME_OUT_MILLIS = 60000L

        var timeOutTime: Long? = null
    }

    private val auth = Firebase.auth
    private var storedVerificationId: String = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var lastPhoneNumberRequested: String = ""
    private val isSignedIn = MutableStateFlow(auth.currentUser != null)
    private val authStateListener = FirebaseAuth.AuthStateListener {
        isSignedIn.value = it.currentUser != null
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    fun finalize() {
        auth.removeAuthStateListener(authStateListener)
    }

    suspend fun verifyPhoneNumber(phoneNumber: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            val currentActivity = activityProvider.currentActivity
            val hasCodeBeenSent = AtomicBoolean(false)
            when {
                currentActivity == null -> continuation.resume(Result.failure(GenericException()))
                else -> {
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setActivity(currentActivity)
                        .setTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
                        .setCallbacks(
                            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    auth.signInWithCredential(credential)
                                        .addOnCompleteListener { task ->
                                            setTimeoutTime()
                                            continuation.resume(
                                                if (task.isSuccessful) {
                                                    Result.success(true)
                                                } else {
                                                    Result.failure(mapException(task.exception))
                                                }
                                            )
                                        }
                                }

                                override fun onVerificationFailed(exception: FirebaseException) {
                                    setTimeoutTime()
                                    continuation.resume(Result.failure(mapException(exception)))
                                }

                                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                                    storedVerificationId = verificationId
                                    resendToken = token
                                    if (!hasCodeBeenSent.get()) {
                                        hasCodeBeenSent.set(true)
                                        setTimeoutTime()
                                        continuation.resume(Result.success(false))
                                    }
                                }

                                override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                                    storedVerificationId = verificationId
                                    if (!hasCodeBeenSent.get()) {
                                        hasCodeBeenSent.set(true)
                                        continuation.resume(Result.success(false))
                                    }
                                }
                            }
                        )
                    if (lastPhoneNumberRequested == phoneNumber && resendToken != null) {
                        options.setForceResendingToken(resendToken!!)
                    }
                    lastPhoneNumberRequested = phoneNumber
                    PhoneAuthProvider.verifyPhoneNumber(options.build())
                }
            }
        }

    suspend fun verifyCode(code: String): Result<Unit> =
        suspendCoroutine { continuation ->
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)

            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    continuation.resume(
                        if (task.isSuccessful) {
                            Result.success(Unit)
                        } else {
                            Result.failure(mapException(task.exception))
                        }
                    )
                }
        }

    fun getTimeoutTimeMillis() = timeOutTime

    fun getSignedInState(): Flow<Boolean> = isSignedIn

    fun isSignedIn(): Boolean = isSignedIn.value

    fun signOut() {
        auth.signOut()
    }

    suspend fun deleteAccount(): Result<Unit> =
        suspendCoroutine { continuation ->
            auth.currentUser?.delete()
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(Result.failure(mapException(it.exception)))
                    }
                } ?: continuation.resume(Result.failure(GenericException()))
        }

    private fun setTimeoutTime() {
        timeOutTime = System.currentTimeMillis() + TIME_OUT_MILLIS
    }

    private fun mapException(exception: Exception?): ProgressException =
        when (exception) {
            is FirebaseTooManyRequestsException -> TooManyRequestsException()
            is FirebaseAuthInvalidCredentialsException -> InvalidNumberException()
            else -> GenericException()
        }
}