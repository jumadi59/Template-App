package com.app.core.network

import android.os.Build
import androidx.core.os.BuildCompat
import com.app.core.BuildConfig
import com.app.core.Constants
import com.app.core.extensions.toDataClass
import com.app.core.model.Token
import com.app.core.pref.Prefs
import com.app.core.pref.getString
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject


/**
 * Created by JJ date on 20/07/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = Prefs.getString(Constants.ACCESS_TOKEN).toDataClass<Token>()
        val request = chain.request().newBuilder()
        request.addHeader("User-Agent", String.format(
                Locale.getDefault(), "%s/%s (Android %s; %s; %s %s; %s)", "", BuildConfig.VERSION_NAME,
                Build.VERSION.RELEASE,
                Build.MODEL,
                Build.BRAND,
                Build.DEVICE,
                Locale.getDefault().language
            ))
        if (!token?.token.isNullOrEmpty()) {
            request.header("Authorization", token!!.authorization()).build()
        }
        return chain.proceed(request.build())
    }

}