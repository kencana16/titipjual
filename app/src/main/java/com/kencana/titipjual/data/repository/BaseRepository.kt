package com.kencana.titipjual.data.repository

import com.kencana.titipjual.data.network.SafeApiCall
import com.kencana.titipjual.data.network.api.BaseApi

abstract class BaseRepository(private val api: BaseApi) : SafeApiCall {

    suspend fun logout() = safeApiCall {
        api.logout()
    }
}
