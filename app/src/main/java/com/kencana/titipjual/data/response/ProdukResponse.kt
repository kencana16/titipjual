package com.kencana.titipjual.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProdukResponse(

    @field:SerializedName("data")
    val data: ProdukData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

@Parcelize
data class ProdukItem(

    @field:SerializedName("date_modified")
    val dateModified: String? = null,

    @field:SerializedName("id_barang")
    val idBarang: String? = null,

    @field:SerializedName("satuan")
    var satuan: String? = null,

    @field:SerializedName("date_created")
    val dateCreated: String? = null,

    @field:SerializedName("harga_satuan_normal")
    var hargaSatuanNormal: String? = null,

    @field:SerializedName("nama_barang")
    var namaBarang: String? = null,

    @field:SerializedName("harga_satuan_reseller")
    var hargaSatuanReseller: String? = null
) : Parcelable

@Parcelize
data class ProdukData(

    @field:SerializedName("produk")
    val produk: List<ProdukItem?>? = null
) : Parcelable
