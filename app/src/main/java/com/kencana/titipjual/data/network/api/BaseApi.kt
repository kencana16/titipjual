package com.kencana.titipjual.data.network.api

import com.kencana.titipjual.data.response.*
import com.kencana.titipjual.ui.data.model.LoginResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface BaseApi {

    @FormUrlEncoded
    @POST("auth")
    suspend fun login(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("user/{id}")
    suspend fun getUser(
        @Path("id") id: String
    ): LoginResponse

    @POST("logout")
    suspend fun logout(): ResponseBody

    // //////////////////////////////////////

    @GET("dashboard")
    suspend fun getIncome(): DashboardResponse

    // //////////////////////////////////////
    @GET("penjual")
    suspend fun getSeller(
        @Query("id") id: String,
    ): SellerResponse

    @POST("penjual")
    suspend fun addSeller(
        @Body item: PenjualItem,
    ): SellerResponse

    @PUT("penjual")
    suspend fun editSeller(
        @Body item: PenjualItem,
    ): SellerResponse

    @DELETE("penjual/{id}")
    suspend fun deleteSeller(
        @Path("id") id: String,
    ): SellerResponse

    // //////////////////////////////////////
    @GET("penjualan")
    suspend fun getSales(
        @Query("id") id: String,
        @Query("seller_id") seller_id: String,
        @Query("date") date: String
    ): SalesResponse

    @POST("penjualan")
    suspend fun addSales(
        @Body item: PenjualanItem
    ): SalesResponse

    @PUT("penjualan/rekap")
    suspend fun updateProductSales(
        @Body item: List<DetailBarangItemPenjualan?>?
    ): SalesResponse

    @GET("penjualan/rating")
    suspend fun getProductRating(
        @Query("id_barang") product_id: String,
        @Query("id_penjual") seller_id: String,
    ): SalesProductRatingResponse

    @FormUrlEncoded
    @POST("penjualan/bayar")
    suspend fun addPaymentPenjualan(
        @Field("id_penjualan") id_penjualan: String,
        @Field("jumlah_uang") jumlah_uang: String
    ): SalesResponse

    // //////////////////////////////////////
    @GET("pesanan")
    suspend fun getOrder(
        @Query("date") date: String
    ): OrderResponse

    @POST("pesanan")
    suspend fun addOrder(
        @Body item: PesananItem
    ): OrderResponse

    @PUT("pesanan/edit")
    suspend fun updateProductOrder(
        @Body item: MutableList<DetailBarangItemPesanan?>?
    ): OrderResponse

    @FormUrlEncoded
    @POST("pesanan/bayar")
    suspend fun addPaymentPesanan(
        @Field("id_pesanan") id_pesanan: String,
        @Field("jumlah_uang") jumlah_uang: String
    ): OrderResponse

    // //////////////////////////////////////
    @GET("produk")
    suspend fun getProduct(
        @Query("id") id: String,
    ): ProdukResponse

    @POST("produk")
    suspend fun addProduct(
        @Body item: ProdukItem,
    ): ProdukResponse

    @PUT("produk")
    suspend fun editProduct(
        @Body item: ProdukItem,
    ): ProdukResponse

    @DELETE("produk/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String,
    ): ProdukResponse

    // //////////////////////////////////////
}
