package com.eaccid.musimpa.ui.mainscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.repository.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    fun login() {
        _uiState.value = MainScreenState.NoData
        viewModelScope.launch {
            try {
                val authentication = authenticationRepository.login()
                if (authentication.success != null && authentication.success)
                    _uiState.value = MainScreenState.Success
                else
                    _uiState.value = MainScreenState.Error
            } catch (exception: Exception) {
                _uiState.value = MainScreenState.Error
            }
        }
    }

    private val _uiState: MutableStateFlow<MainScreenState> =
        MutableStateFlow(MainScreenState.NoData)
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()


    override fun onCleared() {
        super.onCleared()
        Log.i("MainScreenViewModel", "is cleared")
    }
}

