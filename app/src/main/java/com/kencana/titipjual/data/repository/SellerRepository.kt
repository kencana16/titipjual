package com.kencana.titipjual.data.repository

import com.kencana.titipjual.data.network.api.BaseApi
import com.kencana.titipjual.data.response.PenjualItem
import javax.inject.Inject

class SellerRepository @Inject constructor(
    private val api: BaseApi
) :
    BaseRepository(api) {
    suspend fun getSeller(id: String = "") = safeApiCall { api.getSeller(id = "") }
    suspend fun addSeller(data: PenjualItem) = safeApiCall { api.addSeller(data) }
    suspend fun editSeller(data: PenjualItem) = safeApiCall { api.editSeller(data) }
    suspend fun deleteSeller(id: String = "") = safeApiCall { api.deleteSeller(id) }
}
