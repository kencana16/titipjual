package com.kencana.titipjual.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SalesResponse(

    @field:SerializedName("data")
    val data: SalesData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class SalesData(

    @field:SerializedName("penjualan")
    val penjualan: List<PenjualanItem?>? = null
)

@Parcelize
data class DetailPembayaranItemPenjualan(

    @field:SerializedName("id_penjualan")
    val idPenjualan: String? = null,

    @field:SerializedName("date_modified")
    val dateModified: String? = null,

    @field:SerializedName("date_created")
    val dateCreated: String? = null,

    @field:SerializedName("id_pembayaran_penjualan")
    val idPembayaranPenjualan: String? = null,

    @field:SerializedName("jumlah_uang")
    val jumlahUang: String? = null,

    @field:SerializedName("tanggal")
    val tanggal: String? = null
) : Parcelable

@Parcelize
data class DetailBarangItemPenjualan(

    @field:SerializedName("id_penjualan")
    val idPenjualan: String? = null,

    @field:SerializedName("id_detail_penjualan")
    val idDetailPenjualan: String? = null,

    @field:SerializedName("date_modified")
    val dateModified: String? = null,

    @field:SerializedName("id_barang")
    var idBarang: String? = null,

    @field:SerializedName("nama_barang")
    var namaBarang: String? = null,

    @field:SerializedName("date_created")
    val dateCreated: String? = null,

    @field:SerializedName("jml_produk")
    var jmlProduk: String? = null,

    @field:SerializedName("jml_terjual")
    var jmlTerjual: String? = null,

    @field:SerializedName("jml_uang")
    var jmlUang: String? = null,

    @field:SerializedName("harga_satuan_reseller")
    var hargaSatuanReseller: String? = null
) : Parcelable

@Parcelize
data class PenjualanItem(

    @field:SerializedName("nama_penjual")
    val namaPenjual: String? = null,

    @field:SerializedName("detail_pembayaran")
    val detailPembayaran: MutableList<DetailPembayaranItemPenjualan?>? = null,

    @field:SerializedName("date_created")
    val dateCreated: String? = null,

    @field:SerializedName("dibayar")
    val dibayar: String? = null,

    @field:SerializedName("jml_terjual")
    val jmlTerjual: String? = null,

    @field:SerializedName("id_penjualan")
    val idPenjualan: String? = null,

    @field:SerializedName("total")
    val total: String? = null,

    @field:SerializedName("date_modified")
    val dateModified: String? = null,

    @field:SerializedName("detail_barang")
    var detailBarang: MutableList<DetailBarangItemPenjualan?>? = null,

    @field:SerializedName("id_penjual")
    val idPenjual: String? = null,

    @field:SerializedName("jml_produk")
    val jmlProduk: String? = null,

    @field:SerializedName("tgl_penjualan")
    val tglPenjualan: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("no_hp")
    val noHp: String? = null
) : Parcelable
