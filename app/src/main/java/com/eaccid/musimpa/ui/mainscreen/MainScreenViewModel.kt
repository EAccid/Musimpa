package com.eaccid.musimpa.ui.mainscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.network.ApiResponse
import com.eaccid.musimpa.repository.AuthenticationRepository
import com.eaccid.musimpa.utils.AUTHENTICATE_REQUEST_TOKEN_URL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainScreenViewState> =
        MutableStateFlow(MainScreenViewState(state = MainScreenState.Success))
    val uiState: StateFlow<MainScreenViewState> = _uiState.asStateFlow()

    init {
        Log.i("MainScreenViewModel twicetest ----------------- ", " $this is created 1")
        if (authenticationRepository.isUserLoggedIn())
            _uiState.update { it.copy(state = MainScreenState.Success) }
        else {
            _uiState.update { it.copy(state = MainScreenState.NoData) }
        }
    }

    fun login() {
        viewModelScope.launch {
            val apiResponse = authenticationRepository.getToken()
            if (apiResponse is ApiResponse.Success) {
                _uiState.update {
                    it.copy(
                        state = MainScreenState.OnSiteLogin, loginData = LoginOnSiteData(
                            (AUTHENTICATE_REQUEST_TOKEN_URL + apiResponse.data.request_token)
                        )
                    )
                }
            } else _uiState.update { it.copy(state = MainScreenState.Error) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MainScreenViewModel twicetest ----------------- ", " $this is cleared 1")
    }

    fun onWebAction(succeed: Boolean) {
        Log.i("MusimpaApp", "onWebAction = $succeed")
        if (succeed) {
            viewModelScope.launch {
                val apiResponse = authenticationRepository.createSessionId()
                if (apiResponse is ApiResponse.Success) {
                    _uiState.update { it.copy(state = MainScreenState.Success) }
                    return@launch
                }
            }
        }
        _uiState.update { it.copy(state = MainScreenState.Error) }
    }
}

class MainScreenViewModelFactory(
    private val authenticationRepository: AuthenticationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainScreenViewModel(authenticationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
