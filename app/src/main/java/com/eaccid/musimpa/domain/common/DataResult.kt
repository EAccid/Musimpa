package com.eaccid.musimpa.domain.common

sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Failure(val error: Throwable, val message: String? = null) : DataResult<Nothing>()
    data object NetworkError : DataResult<Nothing>()
}