package com.kolown.network.adapter

import com.kolown.network.model.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class ApiResponseCallAdapter<T>(
    private val resultType: Type,
): CallAdapter<T, Call<ApiResponse<T>>> {
    override fun responseType(): Type {
        return resultType
    }

    override fun adapt(call: Call<T>): Call<ApiResponse<T>> {
        return ApiResponseCallDelegate(call, resultType)
    }
}