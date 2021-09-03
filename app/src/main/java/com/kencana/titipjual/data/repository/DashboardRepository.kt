package com.kencana.titipjual.data.repository

import com.kencana.titipjual.data.network.api.BaseApi
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val api: BaseApi
) :
    BaseRepository(api) {
    suspend fun getIncome() = safeApiCall { api.getIncome() }
}
