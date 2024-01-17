package com.eaccid.musimpa.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eaccid.musimpa.BuildConfig
import com.eaccid.musimpa.LocalPreferences
import com.eaccid.musimpa.entities.Authentication
import com.eaccid.musimpa.utils.API_VERSION
import com.eaccid.musimpa.utils.EMPTY_STRING_VALUE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthenticationRepositoryImpl(private val serviceAPI: TMDbServiceAPI, private val preferences: LocalPreferences
) :
    AuthenticationRepository {

    //TODO handle responses and status mvvm properly

    private val _isSucceed = MutableLiveData<Boolean>()
    val isSucceed: LiveData<Boolean> get() = _isSucceed

    override fun login(){
        val params = mapOf("api_key" to BuildConfig.THE_MOVIE_DB_API_KEY)
        val authenticationCall: Call<Authentication> = serviceAPI.requestToken(API_VERSION, params)
        _isSucceed.value = false
        authenticationCall.enqueue(object : Callback<Authentication> {
            override fun onResponse(
                call: Call<Authentication>,
                response: Response<Authentication>
            ) {
                val auth: Authentication? = response.body()
                Log.i("Authorization", auth?.toString() ?: "empty body")
                val success = auth?.success ?: false
                preferences.saveString(
                    LocalPreferences.SharedKeys.TOKEN.key,
                    auth?.request_token ?: EMPTY_STRING_VALUE
                )
                if (success) {
//                    redirectToAuthenticateWebView
                    _isSucceed.value = true
                    auth?.request_token ?: EMPTY_STRING_VALUE
                }
            }

            override fun onFailure(call: Call<Authentication>, t: Throwable) {
                Log.i("Authorization", "Failed")
                preferences.saveString(LocalPreferences.SharedKeys.TOKEN.key, EMPTY_STRING_VALUE)
                _isSucceed.value = false
            }
        })
    }

}