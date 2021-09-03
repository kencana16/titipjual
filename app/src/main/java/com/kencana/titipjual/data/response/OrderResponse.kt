package com.kencana.titipjual.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class OrderResponse(

    @field:SerializedName("data")
    val data: PesananData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

@Parcelize
data class DetailBarangItemPesanan(

    @field:SerializedName("id_pesanan")
    val idPesanan: String? = null,

    @field:SerializedName("date_modified")
    val dateModified: String? = null,

    @field:SerializedName("id_barang")
    val idBarang: String? = null,

    @field:SerializedName("subtotal")
    var subtotal: String? = null,

    @field:SerializedName("date_created")
    val dateCreated: String? = null,

    @field:SerializedName("harga_satuan_normal")
    val hargaSatuanNormal: String? = null,

    @field:SerializedName("nama_barang")
    val namaBarang: String? = null,

    @field:SerializedName("harga_satuan_reseller")
    val hargaSatuanReseller: String? = null,

    @field:SerializedName("harga_satuan")
    var hargaSatuan: String? = null,

    @field:SerializedName("id_detail_pesanan")
    val idDetailPesanan: String? = null,

    @field:SerializedName("jml_barang")
    val jmlBarang: String? = null
) : Parcelable

@Parcelize
data class DetailPembayaranItemPesanan(

    @field:SerializedName("id_pesanan")
    val idPesanan: String? = null,

    @field:SerializedName("date_modified")
    val dateModified: String? = null,

    @field:SerializedName("date_created")
    val dateCreated: String? = null,

    @field:SerializedName("jumlah_uang")
    val jumlahUang: String? = null,

    @field:SerializedName("tanggal")
    val tanggal: String? = null,

    @field:SerializedName("id_pembayaran_pesanan")
    val idPembayaranPesanan: String? = null
) : Parcelable

data class PesananData(

    @field:SerializedName("pesanan")
    val pesanan: List<PesananItem?>? = null
)

@Parcelize
data class PesananItem(

    @field:SerializedName("detail_pembayaran")
    val detailPembayaran: List<DetailPembayaranItemPesanan?>? = null,

    @field:SerializedName("date_created")
    val dateCreated: String? = null,

    @field:SerializedName("dibayar")
    val dibayar: String? = null,

    @field:SerializedName("jenis_harga")
    val jenisHarga: String? = null,

    @field:SerializedName("tgl_diambil")
    val tglDiambil: String? = null,

    @field:SerializedName("tgl_dipesan")
    val tglDipesan: String? = null,

    @field:SerializedName("no_hp_pemesan")
    val noHpPemesan: String? = null,

    @field:SerializedName("total")
    val total: String? = null,

    @field:SerializedName("id_pesanan")
    val idPesanan: String? = null,

    @field:SerializedName("date_modified")
    val dateModified: String? = null,

    @field:SerializedName("detail_barang")
    var detailBarang: List<DetailBarangItemPesanan?>? = null,

    @field:SerializedName("id_penjual")
    val idPenjual: String? = null,

    @field:SerializedName("jml_produk")
    val jmlProduk: String? = null,

    @field:SerializedName("nama_pemesan")
    val namaPemesan: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null
) : Parcelable {
    fun strStatus(): String? {
        when (status) {
            "0" -> return "Belum dibuat"
            "1" -> return "Belum dibayar"
            "2" -> return "Selesai"
            "3" -> return "Dibatalkan"
            else -> return null
        }
    }
}
