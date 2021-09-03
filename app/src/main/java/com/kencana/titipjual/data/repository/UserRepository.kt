package com.kencana.titipjual.data.repository

import com.kencana.titipjual.data.UserPreferences
import com.kencana.titipjual.data.network.api.BaseApi
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: BaseApi,
    private val preferences: UserPreferences
) :
    BaseRepository(api) {

    suspend fun getUser() = safeApiCall { api.getUser(preferences.userId.first()!!) }
}
