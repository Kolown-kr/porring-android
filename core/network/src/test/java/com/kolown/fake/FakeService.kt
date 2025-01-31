package com.kolown.fake

import com.kolown.porring.core.network.model.ApiResponse
import org.junit.Ignore
import retrofit2.http.GET
import retrofit2.http.Path

@Ignore("test instance")
internal interface FakeService {

    @GET("test/{name}")
    suspend fun get(
        @Path("name") name: String
    ): ApiResponse<Fake>

    @GET("test")
    suspend fun getFakes(): ApiResponse<Fakes>
}