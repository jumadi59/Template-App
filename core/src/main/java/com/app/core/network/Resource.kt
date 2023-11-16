package com.app.core.network


/**
 * Created by Anonim date on 27/06/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
sealed class Resource<out R> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val message: String, val statusCode: Any, val  data: T? = null) : Resource<T>()
    data class Loading<out T>(val data: T? = null) : Resource<T>()
}
