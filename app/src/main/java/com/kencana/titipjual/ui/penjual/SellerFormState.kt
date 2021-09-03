package com.kencana.titipjual.ui.penjual

data class SellerFormState(
    val namaError: String? = null,
    val hpError: String? = null,
    val alamatError: String? = null,
    val isDataValid: Boolean = false
)
