package com.app.core.model

import android.os.Parcelable
import com.app.core.network.response.GeneralResponse
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Created by Anonim date on 31/08/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
@Parcelize
data class Token(
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("types")
    val typeToken: String? = null,
    @SerializedName("expiry")
    val expiresIn: String? = null
) : GeneralResponse(), Parcelable {
    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun isExpired() = try {
        Jwt(token!!).isExpired()
    } catch (e: Exception) {
        false
    }

    fun authorization() : String = "$typeToken $token"
}