package com.kencana.titipjual.data.network

import com.kencana.titipjual.ui.data.model.LoginResponse
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TitipJualApi {

    @FormUrlEncoded
    @POST("auth")
    suspend fun login(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("logout")
    suspend fun logout(): ResponseBody
}
