package com.eaccid.musimpa.domain.common

import com.eaccid.musimpa.data.remote.ApiResponse

fun <T> ApiResponse<T>.toDataResult(): DataResult<T> = when (this) {
    is ApiResponse.Success -> DataResult.Success(data)
    is ApiResponse.Error -> DataResult.Failure(
        error ?: Exception(message ?: "Unknown error"),
        message
    )
    ApiResponse.NetworkError -> DataResult.NetworkError
}

inline fun <T> ApiResponse<T>.handle(
    onSuccess: (T) -> DataResult<T>,
    noinline onError: ((Throwable?, String?) -> DataResult<T>)? = null,
    noinline onNetworkError: (() -> DataResult<T>)? = null
): DataResult<T> {
    return when (this) {
        is ApiResponse.Success -> onSuccess(data)
        is ApiResponse.Error -> onError?.invoke(error, message)
            ?: DataResult.Failure(error ?: Exception(message ?: "Unknown error"), message)
        is ApiResponse.NetworkError -> onNetworkError?.invoke() ?: DataResult.NetworkError
    }
}

inline fun <T, R> ApiResponse<T>.handleReturn(
    onSuccess: (T) -> DataResult<R>,
    noinline onError: ((Throwable?, String?) -> DataResult<R>)? = null,
    noinline onNetworkError: (() -> DataResult<R>)? = null
): DataResult<R> {
    return when (this) {
        is ApiResponse.Success -> onSuccess(data)
        is ApiResponse.Error -> onError?.invoke(error, message)
            ?: DataResult.Failure(error ?: Exception(message ?: "Unknown error"), message)
        is ApiResponse.NetworkError -> onNetworkError?.invoke() ?: DataResult.NetworkError
    }
}

inline fun <T> DataResult<T>.handle(
    onSuccess: (T) -> Unit,
    onFailure: (Throwable, String?) -> Unit = { _, _ -> },
    onNetworkError: () -> Unit = {}
) {
   return when (this) {
        is DataResult.Success -> onSuccess(data)
        is DataResult.Failure -> onFailure(error, message)
        is DataResult.NetworkError -> onNetworkError()
    }
}

inline fun <T, R> DataResult<T>.handleReturn(
    onSuccess: (T) -> R,
    onFailure: (Throwable, String?) -> R,
    onNetworkError: () -> R
): R = when (this) {
    is DataResult.Success -> onSuccess(data)
    is DataResult.Failure -> onFailure(error, message)
    is DataResult.NetworkError -> onNetworkError()
}