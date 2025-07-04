package com.eaccid.musimpa.data.remote

import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * a little help:
 * https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912
 */

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(
        val code: Int? = null,
        val message: String? = null,
        val error: Throwable? = null
    ) : ApiResponse<Nothing>()

    data object NetworkError : ApiResponse<Nothing>()
}

inline fun <T> safeApiRequest(apiCall: () -> T): ApiResponse<T> {
    try {
        return ApiResponse.Success(apiCall())
    } catch (throwable: Throwable) {
        if (throwable is CancellationException) throw throwable

        return when (throwable) {
            is IOException -> ApiResponse.NetworkError
            is HttpException -> {
                val code = throwable.code()
                val errorMessage = try {
                    throwable.response()?.errorBody()?.string()
                } catch (e: Exception) {
                    throwable.message()
                }
                ApiResponse.Error(code, errorMessage, throwable)
            }

            else -> ApiResponse.Error(null, throwable.message, throwable)
        }
    }
}

fun ApiResponse.Error.getDisplayMessage(): String =
    message ?: error?.localizedMessage ?: "Unknown error"