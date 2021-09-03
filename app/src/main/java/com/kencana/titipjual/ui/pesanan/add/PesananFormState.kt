package com.kencana.titipjual.ui.pesanan.add

data class PesananFormState(
    val tglError: String? = null,
    val namaError: String? = null,
    val hpError: String? = null,
    val alamatEror: String? = null,
    val hargaError: String? = null,
    val barangError: String? = null,
    val isDataValid: Boolean = false
)
