package com.kolown.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kolown.porring.core.network.adapter.ApiResponseCallAdapterFactory
import com.kolown.rule.CoroutineTestRule
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Rule
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

abstract class BaseApiTest<T> {

    @JvmField
    @Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setServer() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun shutdownServer() {
        mockWebServer.shutdown()
    }

    fun enqueueResponse(fileName: String, responseCode: Int = 200) {
        enqueueResponseImpl(fileName, responseCode)
    }

    private fun enqueueResponseImpl(
        fileName: String,
        responseCode: Int = 200,
        headers: Map<String, String> = emptyMap()
    ) {
        val inputStream = javaClass.classLoader!!.getResourceAsStream("response/$fileName")
        val source = inputStream.source().buffer()
        val response = MockResponse()
            .setResponseCode(responseCode)
            .setBody(source.readString(Charsets.UTF_8))
        for ((key, value) in headers) {
            response.addHeader(key, value)
        }
        mockWebServer.enqueue(response)
    }

    fun createService(clazz: Class<T>): T =
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .build()
            .create(clazz)
}