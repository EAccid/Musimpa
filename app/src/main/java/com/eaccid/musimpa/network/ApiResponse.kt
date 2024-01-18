package com.eaccid.musimpa.network

import retrofit2.HttpException
import java.io.IOException

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

    object NetworkError : ApiResponse<Nothing>()
}
inline fun <T> safeApiRequest(apiCall: () -> T): ApiResponse<T> {
    try {
        return ApiResponse.Success(apiCall())
    } catch (throwable: Throwable) {
        return when (throwable) {
            is IOException -> ApiResponse.NetworkError
            is HttpException -> {
                val code = throwable.code()
                val errorMessage = throwable.message()
                ApiResponse.Error(code, errorMessage)
            }

            else -> {
                ApiResponse.Error(null, null, throwable)
            }
        }
    }
}