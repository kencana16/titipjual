package com.kencana.titipjual.ui.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("msg")
    val msg: String? = null,

    @SerializedName("data")
    val data: UserData? = null,

    @SerializedName("success")
    val success: Boolean? = null
)

data class User(

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("no_hp")
    val noHp: String? = null,

    @SerializedName("foto")
    val foto: String? = null,

    @SerializedName("id_penjual")
    val idPenjual: String? = null,

    @SerializedName("id_user")
    val idUser: String? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("token")
    val token: String? = null
)

data class UserData(

    @SerializedName("user")
    val user: User? = null
)
