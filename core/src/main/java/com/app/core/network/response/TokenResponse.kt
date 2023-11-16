package com.app.core.network.response

import android.os.Parcelable
import com.app.core.model.Token
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Created by JJ date on 10/10/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
@Parcelize
data class TokenResponse(
    @SerializedName("access_token"  ) var accessToken  : String? = null,
    @SerializedName("token_type"    ) var tokenType    : String? = null,
    @SerializedName("refresh_token" ) var refreshToken : String? = null,
    @SerializedName("expires_in"    ) var expiresIn    : Int?    = null,
    @SerializedName("scope"         ) var scope        : String? = null,
    @SerializedName("organization"  ) var organization : String? = null,
    @SerializedName("jti"           ) var jti          : String? = null
) : GeneralResponse(), Parcelable
