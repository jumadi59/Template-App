package com.app.core.network

import android.annotation.SuppressLint
import android.content.Context
import com.app.core.util.Environment
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.reflect.KClass


/**
 * Created by JJ date on 12/07/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
open class RetrofitClient @Inject constructor(private val environment: Environment, private val level: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE, val isPlan: Boolean = false) {

    private val interceptors = ArrayList<Interceptor>()

    fun addInterceptor(interceptor: Interceptor) {
        if (interceptors.find { it == interceptor } == null) {
            interceptors.add(interceptor)
        }
    }

    fun getClient() = (if (isPlan) getUnsafeOkHttpClient() else OkHttpClient.Builder()).addInterceptor(HttpLoggingInterceptor().apply {
        level = this@RetrofitClient.level
    }).addInterceptor(Interceptor { chain ->

        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept-Language", Locale.getDefault().language)
            .build()
        //.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + 2).build()
        try {
            val response = chain.proceed(request)
            response
        } catch (e: UnknownHostException) {
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(0)
                .message("No address associated with hostname")
                .body("No address associated with hostname".toResponseBody(null))
                .build()
        } catch (e: ConnectException) {
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(1)
                .message("Connection Error")
                .body(e.toString().toResponseBody(null))
                .build()
        } catch (e: IOException) {
            e.printStackTrace()
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(408)
                .message("Timeout")
                .body(e.toString().toResponseBody(null))
                .build()
        }
    }).readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(true)

    fun <T : Any> getRetrofit(klazz: KClass<T>): T = Retrofit.Builder().apply {
        baseUrl(environment.getBaseApi() + environment.getPath())
        client(getClient().build())
        addConverterFactory(GsonConverterFactory.create())
    }.build().create(klazz.java)

    fun <T : Any> getRetrofitPath(klazz: KClass<T>, path: String): T = Retrofit.Builder().apply {
        baseUrl(environment.getBaseApi() + "/$path/")
        client(getClient().build())
        addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
    }.build().create(klazz.java)

    fun <T : Any> getRetrofit(klazz: KClass<T>, baseUrl: String): T = Retrofit.Builder().apply {
        baseUrl(baseUrl)
        client(getClient().build())
        addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
    }.build().create(klazz.java)

    fun <T : Any> getRetrofit(klazz: KClass<T>, client: OkHttpClient): T = Retrofit.Builder().apply {
        baseUrl(environment.getBaseApi())
        client(client)
        addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
    }.build().create(klazz.java)

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            interceptors.forEach { builder.addInterceptor(it) }
            builder
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }
}