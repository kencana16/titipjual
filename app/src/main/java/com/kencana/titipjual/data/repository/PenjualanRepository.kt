package com.kencana.titipjual.data.repository

import com.kencana.titipjual.data.network.api.BaseApi
import com.kencana.titipjual.data.response.PenjualanItem
import javax.inject.Inject

class PenjualanRepository @Inject constructor(
    private val api: BaseApi
) :
    BaseRepository(api) {
    suspend fun getSales(id: String = "", seller: String = "", date: String = "") =
        safeApiCall { api.getSales(id, seller, date) }

    suspend fun addSales(item: PenjualanItem) = safeApiCall { api.addSales(item) }
    suspend fun updateProduct(item: PenjualanItem) = safeApiCall {
        api.updateProductSales(
            item.detailBarang
        )
    }

    suspend fun addPayment(pay: String, penjualanId: String) =
        safeApiCall { api.addPaymentPenjualan(pay, penjualanId) }
}
