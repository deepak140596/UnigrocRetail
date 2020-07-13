package com.avvnapps.unigrocretail.database.network


import com.avvnapps.unigrocretail.models.GeoIpResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface GeoIpService {
    //@Headers("User-Agent: java-ipapi-client")
    @GET("json")
    fun getGeoIp(): Call<GeoIpResponseModel>

    @GET("json/{ipAddress}")
    @Headers("User-Agent: java-ipapi-client")
    fun getGeoIp(@Path("ipAddress") ipAddress: String): Call<GeoIpResponseModel>
}