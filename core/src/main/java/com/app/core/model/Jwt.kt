package com.app.core.model

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import java.nio.charset.StandardCharsets


/**
 * Created by Anonim date on 18/12/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
class Jwt(private val token: String) {

    private val userData by lazy {
        val userData = String(Base64.decode(token.split(".")[1], Base64.DEFAULT), StandardCharsets.UTF_8)
        JsonParser().parse(userData).asJsonObject
    }

    fun getUserData(): JwtPayload {
        gson.toJson(userData, Jwt::class.java)
        return gson.fromJson(userData, JwtPayload::class.java)
    }

    fun isExpired(): Boolean {
        return userData.asJsonObject.get("exp").asLong < (System.currentTimeMillis() / 1000)
    }

    companion object {

        @JvmStatic
        private val gson = Gson()
    }
}

data class JwtPayload(

    @SerializedName("iat")
    val iat: Int,

    @SerializedName("operador")
    val operador: Operador
)

data class Operador(

    @SerializedName("id")
    val id: Int,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("usuario")
    val usuario: String
)