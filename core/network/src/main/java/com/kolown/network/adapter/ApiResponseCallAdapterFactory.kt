package com.kolown.network.adapter

import com.kolown.network.model.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class ApiResponseCallAdapterFactory
    : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotation: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        when(getRawType(returnType)) {
            Call::class.java -> {
                if (returnType !is ParameterizedType) {
                    return null
                }
                val callType = getParameterUpperBound(0, returnType)
                val rawType = getRawType(callType)
                if (rawType != ApiResponse::class.java) {
                    return null
                }

                val resultType = getParameterUpperBound(0, callType as ParameterizedType)

                return ApiResponseCallAdapter<Any>(resultType)
            }

            else -> return null
        }
    }
}