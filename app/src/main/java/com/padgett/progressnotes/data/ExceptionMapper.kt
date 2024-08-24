package com.padgett.progressnotes.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.padgett.progressnotes.domain.common.exceptions.GenericException
import com.padgett.progressnotes.domain.common.exceptions.InvalidNumberException
import com.padgett.progressnotes.domain.common.exceptions.NoNetworkException
import com.padgett.progressnotes.domain.common.exceptions.ProgressException
import com.padgett.progressnotes.domain.common.exceptions.TooManyRequestsException

fun Throwable?.mapToDomainException(): ProgressException =
    when {
        this is FirebaseTooManyRequestsException -> TooManyRequestsException()
        this is FirebaseAuthInvalidCredentialsException -> InvalidNumberException()
        this is FirebaseNetworkException -> NoNetworkException()
        else -> GenericException()
    }