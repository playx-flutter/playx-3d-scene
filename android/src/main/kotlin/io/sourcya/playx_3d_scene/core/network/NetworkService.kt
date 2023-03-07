package io.sourcya.playx_3d_scene.core.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface NetworkService {

    companion object{
        const val BASE_URL = "https://google.com"
    }

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl:String): Response<ResponseBody>


}