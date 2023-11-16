package com.app.core.extensions

import android.util.Log
import com.app.core.network.ApiResponse
import com.app.core.network.response.DataResponse
import com.app.core.network.response.GeneralResponse
import retrofit2.Response


/**
 * Created by JJ date on 11/10/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/


fun <T> Response<T>.toApiResponse() : ApiResponse<T & Any> {
    val response = this
    return when {
        response.code() in 200..299 -> {
            val body = response.errorBody()?.string()
            Log.e("ApiResponse", "${response.code()} errorBody: $body")
            Log.e("ApiResponse","${response.code()} body: ${response.body()}")

            if (response.body() is GeneralResponse) {
                val bodyData = response.body() as GeneralResponse
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Success(response.body()!!)
            }
        }
        else -> {
            if (response.body() is GeneralResponse) {
                val generalResponse = response.body() as GeneralResponse
                if (generalResponse.status == null) generalResponse.status = response.code()

                ApiResponse.Error(generalResponse)
            } else {
                try {
                    val body = response.errorBody()?.string()
                    Log.e("ApiResponse", "${response.code()} $body")
                    ApiResponse.Error(body!!.toDataClass()?: GeneralResponse(response.message(), response.code()))
                } catch (e: Exception) {
                    ApiResponse.Error(GeneralResponse(response.message(), -1))
                }
            }
        }
    }
}

fun <T> Response<DataResponse<T>>.toDataApiResponse() : ApiResponse<T> {
    val response = this
    return when {
        response.code() in 200..299 -> {
            val body = response.errorBody()?.string()
            Log.e("DataApiResponse", "${response.code()}  errorBody: $body")
            Log.e("DataApiResponse","body: ${response.body()}")

            val bodyData = response.body()
            if (bodyData != null) {
                if (bodyData.data != null) ApiResponse.Success(response.body()!!.data!!)
                else ApiResponse.Error(response.body()!!)
            } else {
                ApiResponse.Error(response.body()!!)
            }
        }
        else -> {
            if (response.body() is GeneralResponse) {
                val generalResponse = response.body() as GeneralResponse
                if (generalResponse.status == null) generalResponse.status = response.code()

                ApiResponse.Error(generalResponse)
            } else if (response.body() != null) ApiResponse.Error(response.body()!!)
            else {
                try {
                    val body = response.errorBody()?.string()
                    Log.e("DataApiResponse", "${response.code()}  errorBody: $body")
                    ApiResponse.Error(body!!.toDataClass()?: GeneralResponse(response.message(), response.code()))
                } catch (e: Exception) {
                    ApiResponse.Error(GeneralResponse(response.message(), -1))
                }
            }
        }
    }
}
