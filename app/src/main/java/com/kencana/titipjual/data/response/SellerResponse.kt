package com.kencana.titipjual.data.response

import com.google.gson.annotations.SerializedName

data class SellerResponse(

    @field:SerializedName("data")
    val data: SellerData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class PenjualItem(

    @field:SerializedName("nama_penjual")
    val namaPenjual: String? = null,

    @field:SerializedName("no_hp")
    val noHp: String? = null,

    @field:SerializedName("date_modified")
    val dateModified: String? = null,

    @field:SerializedName("id_penjual")
    val idPenjual: String? = null,

    @field:SerializedName("date_created")
    val dateCreated: String? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null
)

data class SellerData(

    @field:SerializedName("penjual")
    val penjual: List<PenjualItem>? = null
)
