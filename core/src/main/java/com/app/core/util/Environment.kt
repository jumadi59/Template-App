package com.app.core.util

import com.app.core.network.RetrofitClient
import okhttp3.Interceptor


/**
 * Created by JJ date on 10/10/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
interface Environment {

    fun getBaseApi() : String
    fun getPath() : String
    fun getAppPackageName() : String
    fun routeHome() : String
    fun routeDefault() : String
    fun getRetrofitClient() : RetrofitClient
}