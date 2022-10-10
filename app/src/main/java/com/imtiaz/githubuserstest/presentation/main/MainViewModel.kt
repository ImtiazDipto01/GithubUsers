package com.imtiaz.githubuserstest.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _networkStateFlow = MutableSharedFlow<Boolean>(replay = 0)
    val networkStateFlow: SharedFlow<Boolean> = _networkStateFlow

    fun notifyNetworkState(isConnected: Boolean) {
        viewModelScope.launch {
            _networkStateFlow.emit(isConnected)
        }
    }

}