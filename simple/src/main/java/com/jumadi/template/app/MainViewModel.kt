package com.jumadi.template.app

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.network.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(application: Application) : ViewModel() {


    private val _template: MutableStateFlow<ApiResponse<String>> = MutableStateFlow(ApiResponse.Loading)
    val template: StateFlow<ApiResponse<String>> = _template

    init {
        viewModelScope.launch {
            delay(1000)
            _template.emit(ApiResponse.Success("Hello Template App"))
        }
    }
}