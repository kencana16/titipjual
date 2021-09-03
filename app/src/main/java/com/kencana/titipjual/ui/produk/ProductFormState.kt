package com.kencana.titipjual.ui.produk

data class ProductFormState(
    val namaError: String? = null,
    val satuanError: String? = null,
    val hargaNormalError: String? = null,
    val hargaResellerError: String? = null,
    val isDataValid: Boolean = false
)
