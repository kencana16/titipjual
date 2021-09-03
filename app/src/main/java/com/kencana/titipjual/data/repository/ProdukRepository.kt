package com.kencana.titipjual.data.repository

import com.kencana.titipjual.data.network.api.BaseApi
import com.kencana.titipjual.data.response.ProdukItem
import javax.inject.Inject

class ProdukRepository @Inject constructor(
    private val api: BaseApi
) :
    BaseRepository(api) {

    suspend fun getProduct(id: String = "") = safeApiCall { api.getProduct(id = "") }
    suspend fun addProduct(data: ProdukItem) = safeApiCall { api.addProduct(data) }
    suspend fun editProduct(data: ProdukItem) = safeApiCall { api.editProduct(data) }
    suspend fun deleteProduct(id: String = "") = safeApiCall { api.deleteProduct(id) }

    suspend fun getRating(product_id: String, seller_id: String) =
        safeApiCall { api.getProductRating(product_id, seller_id) }
}
