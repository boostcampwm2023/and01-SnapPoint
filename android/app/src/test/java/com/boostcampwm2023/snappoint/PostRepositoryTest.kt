package com.boostcampwm2023.snappoint

import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit


class PostRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var snapPointApi: SnapPointApi
    private lateinit var retrofit: Retrofit


    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

        snapPointApi = retrofit.create(SnapPointApi::class.java)

    }

    @After
    fun tearDown(){
        server.shutdown()
    }

    @Test
    fun getImage_Success() = runTest {
        val response = """
            {
                "image": "asdasd"
            }
        """.trimIndent()

        server.enqueue(MockResponse().setBody(response))

        val imageByteArray = snapPointApi.getImage(
            uri = "http://asdf.com"
        )

        println(imageByteArray)

    }

    @Test
    fun getImageUri_Success() = runTest {
        val response = """
            {
                "uri": "https://aasdasd.com"
            }
        """.trimIndent()

        server.enqueue(MockResponse().setBody(response))

        val imageUri = snapPointApi.getImageUri(
            image = "thisIsImageByte"
        )

        println(imageUri)

    }

    @Test
    fun getImage_Client_Error() = runTest {
        val response = """
            {
                "image": "asdasd"
            }
        """.trimIndent()


        server.enqueue(MockResponse().setBody(response).setResponseCode(400))

        val flow = flowOf(true).map{(snapPointApi.getImage(uri = "http://asdf.com"))}

        launch {
            flow
                .catch {
                    assert(it.message == "HTTP 400 Client Error")
                    assert(it is HttpException)
                }
                .collect{ assert(it.image == "zzz") }
        }

    }

}

