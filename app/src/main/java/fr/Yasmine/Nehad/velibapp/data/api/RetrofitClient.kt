package fr.Yasmine.Nehad.velibapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL =
        "https://velib-metropole-opendata.smovengo.cloud/opendata/Velib_Metropole/"

    val apiService: VelibApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VelibApiService::class.java)
    }
}