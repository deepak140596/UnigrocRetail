package com.avvnapps.unigrocretail.database

import com.avvnapps.unigrocretail.database.network.GeoIpService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeoIpServicesManager {
    private const val BASE_URL = "https://ipapi.co/"

    @JvmStatic
    val geoIpService: GeoIpService
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeoIpService::class.java)
}