package com.avvnapps.unigrocretail.models

data class GeoIp(
    var countryName: String = "",
    var currency: String = "",
    var country: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var isp: String = ""
)