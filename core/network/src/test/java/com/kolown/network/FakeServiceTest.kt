package com.kolown.network

import com.kolown.fake.FakeService
import com.kolown.network.model.ApiResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

internal class FakeServiceTest : BaseApiTest<FakeService>() {

    private lateinit var service: FakeService

    @Before
    fun initService() {
        service = createService(FakeService::class.java)
    }

    @Test
    fun `페이크 서비스에 이름과 함께 Get 요청할때 fake 정보 가져오기`() = runTest {
        enqueueResponse("fake.json")
        val response = service.get("닝닝")
        val responseBody = requireNotNull((response as ApiResponse.Success).data)

        assertEquals(1, responseBody.id)
        assertEquals("닝닝", responseBody.name)
        assertEquals(23, responseBody.age)
    }

    @Test
    fun `페이크 서비스에 Get 요청을 할때 fakes 정보 가져오기`() = runTest {
        enqueueResponse("fakes.json")
        val response = service.getFakes()
        val responseBody = requireNotNull((response as ApiResponse.Success).data)

        assertEquals(5, responseBody.users.size)
        assertEquals(1, responseBody.users[0].userId)
        assertEquals("AAAAA",responseBody.users[0].firstName)
        assertEquals("as23", responseBody.users[0].lastName)
    }

    @Test
    fun `서버에 데이터를 요청 했는데 서버에서 401 에러를 반환할때`() = runTest {
        enqueueResponse("fakes.json", 401)
        val response = service.getFakes()

        assert(response is ApiResponse.Failure.Error)
        assertEquals(401, (response as ApiResponse.Failure.Error).code)
    }
}