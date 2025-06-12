package com.kolown.porring.core.data.api.datasource.local

import com.kolown.porring.core.model.User
import kotlinx.coroutines.flow.Flow

interface LocalUserPrefDatasource {
    suspend fun createUserData(user: User): Result<Unit>
    fun getUserEmail(userId: String): Flow<String>
}