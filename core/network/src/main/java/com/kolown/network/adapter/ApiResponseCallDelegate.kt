package com.kolown.network.adapter

import com.kolown.network.model.ApiResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

internal class ApiResponseCallDelegate<T>(
    private val call: Call<T>,
    private val resultType: Type
) : Call<ApiResponse<T>> {

    override fun enqueue(callback: Callback<ApiResponse<T>>) = synchronized(this) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val apiResponse = response.asApiResponse(resultType)
                callback.onResponse(this@ApiResponseCallDelegate, Response.success(apiResponse))
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                val apiResponse = ApiResponse.Failure.Exception(throwable)
                callback.onResponse(this@ApiResponseCallDelegate, Response.success(apiResponse))
            }
        }
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun execute(): Response<ApiResponse<T>> = runBlocking {
        val response = call.execute()
        val apiResponse = try {
            if (response.code() in 200..299) {
                ApiResponse.Success(response.body() ?: Unit as T)
            } else {
                ApiResponse.Failure.Error(response.code())
            }
        } catch (e: Exception) {
            ApiResponse.Failure.Exception(e)
        }
        Response.success(apiResponse)
    }

    override fun clone(): Call<ApiResponse<T>> = ApiResponseCallDelegate(call.clone(), resultType)

    override fun request(): Request = call.request()
    override fun cancel() = call.cancel()
    override fun timeout(): Timeout = call.timeout()

    override fun isExecuted(): Boolean = call.isExecuted
    override fun isCanceled(): Boolean = call.isCanceled
}