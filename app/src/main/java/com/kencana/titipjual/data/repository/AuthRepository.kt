package com.kencana.titipjual.data.repository

import com.kencana.titipjual.data.UserPreferences
import com.kencana.titipjual.data.network.api.BaseApi
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: BaseApi,
    private val preferences: UserPreferences
) : BaseRepository(api) {

    suspend fun login(
        phone: String,
        password: String
    ) = safeApiCall { api.login(phone, password) }

    suspend fun saveAuthToken(userId: String, authToken: String) {
        preferences.saveUserInfo(userId, authToken)
    }
}
