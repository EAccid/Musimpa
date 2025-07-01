package com.eaccid.musimpa.data.remote.services.interceptors

import com.eaccid.musimpa.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class KeyLanguageQueryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
            .addQueryParameter("language", "en-US")
            .build()

        val newRequest = originalRequest.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}