package com.kencana.titipjual.data.repository

import com.kencana.titipjual.data.network.api.BaseApi
import com.kencana.titipjual.data.response.DetailBarangItemPesanan
import com.kencana.titipjual.data.response.PesananItem
import javax.inject.Inject

class PesananRepository @Inject constructor(
    private val api: BaseApi
) :
    BaseRepository(api) {
    suspend fun getOrder(date: String = "") = safeApiCall { api.getOrder(date) }

    suspend fun addOrder(item: PesananItem) = safeApiCall { api.addOrder(item) }
    suspend fun updateProduct(item: PesananItem) = safeApiCall {
        api.updateProductOrder(
            item.detailBarang as MutableList<DetailBarangItemPesanan?>?
        )
    }

    suspend fun addPayment(pay: String, pesananId: String) =
        safeApiCall { api.addPaymentPesanan(pay, pesananId) }
}
