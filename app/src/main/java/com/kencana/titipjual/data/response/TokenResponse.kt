package com.kencana.titipjual.ui.data.model

import com.google.gson.annotations.SerializedName

data class TokenResponse(

    @SerializedName("access_token")
    val access_token: String?,

    @SerializedName("refresh_token")
    val refresh_token: String?,
)
