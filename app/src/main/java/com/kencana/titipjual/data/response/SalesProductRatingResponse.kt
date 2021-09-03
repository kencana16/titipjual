package com.kencana.titipjual.data.response

import com.google.gson.annotations.SerializedName

data class SalesProductRatingResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
)

data class Data(

    @field:SerializedName("harian")
    val harian: String? = null,

    @field:SerializedName("mingguan")
    val mingguan: String? = null,

    @field:SerializedName("hari")
    val hari: String? = null
)
