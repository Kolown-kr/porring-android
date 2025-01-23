package com.kolown.network.adapter

import com.kolown.network.model.ApiResponse
import retrofit2.Response
import java.lang.reflect.Type

internal fun <T> Response<T>.asApiResponse(
    resultType: Type,
): ApiResponse<T> =
    if (!isSuccessful) {
        parseUnsuccessfulResponse(this)
    } else {
        parseSuccessfulResponse(this, resultType)
    }

@Suppress("UNCHECKED_CAST")
private fun <T> parseUnsuccessfulResponse(
    response: Response<T>,
): ApiResponse.Failure<T> {
    return try {
        ApiResponse.Failure.Error(response.code(), response.errorBody()) as ApiResponse.Failure<T>
    } catch (error: Throwable) {
        ApiResponse.Failure.Exception(error) as ApiResponse.Failure<T>
    }
}

private fun <T> parseSuccessfulResponse(
    response: Response<T>,
    resultType: Type
): ApiResponse<T> {
    val responseBody: T? = response.body()
    if (responseBody == null) {
        if (resultType === Unit::class.java) {
            @Suppress("UNCHECKED_CAST")
            return ApiResponse.Success(Unit) as ApiResponse<T>
        }

        return ApiResponse.Failure.Error(response.code(), response.errorBody())
    }

    return ApiResponse.Success(responseBody)
}