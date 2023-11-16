package com.app.core.network

import com.app.core.network.response.GeneralResponse


/**
 * Created by Anonim date on 27/06/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
sealed class ApiResponse<out R> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val error: GeneralResponse) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}

fun <T> ApiResponse<T>.toResource(data: T? = null) : Resource<T> {
    return when(this) {
        is ApiResponse.Error -> Resource.Error(this.error.message, this.error.status, data)
        ApiResponse.Loading -> Resource.Loading(data)
        is ApiResponse.Success -> Resource.Success(this.data)
    }
}
