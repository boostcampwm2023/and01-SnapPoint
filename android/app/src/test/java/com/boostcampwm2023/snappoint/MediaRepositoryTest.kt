package com.boostcampwm2023.snappoint

import androidx.test.runner.AndroidJUnit4
import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit


class MediaRepositoryTest {

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
    fun getImageTest() = runTest {
        val response = """
            "image"
        """.trimIndent()

        server.enqueue(MockResponse().setBody(response))

        val imageByteArray = snapPointApi.getImage(
            uri = "http://asdf.com"
        )

        println(imageByteArray)

    }

}