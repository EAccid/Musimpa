package com.eaccid.musimpa.ui.mainscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.network.ApiResponse
import com.eaccid.musimpa.repository.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainScreenState> =
        MutableStateFlow(MainScreenState.NoData)
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    init {
        if (authenticationRepository.isUserLoggedIn())
            _uiState.value = MainScreenState.Success
        else {
            _uiState.value = MainScreenState.NoData
        }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = when (authenticationRepository.getToken()) {
                is ApiResponse.Success -> {
                    MainScreenState.NavigateToWebView
                }

                else -> MainScreenState.Error
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MainScreenViewModel", "is cleared")
    }

    fun onWebAction(succeed: Boolean) {
        if (succeed)
            _uiState.value = MainScreenState.Success
        else
            _uiState.value = MainScreenState.Error
    }
}

