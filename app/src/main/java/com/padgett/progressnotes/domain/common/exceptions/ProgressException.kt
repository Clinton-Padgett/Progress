package com.padgett.progressnotes.domain.common.exceptions

sealed class ProgressException : Throwable()

class GenericException : ProgressException()
class InvalidNumberException : ProgressException()
class TooManyRequestsException : ProgressException()