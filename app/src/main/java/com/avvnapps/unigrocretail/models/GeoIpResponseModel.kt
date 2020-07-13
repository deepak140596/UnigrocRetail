package com.avvnapps.unigrocretail.models


import com.google.gson.annotations.SerializedName

data class GeoIpResponseModel(
    @SerializedName("asn")
    val asn: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("continent_code")
    val continentCode: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("country_area")
    val countryArea: Double,
    @SerializedName("country_calling_code")
    val countryCallingCode: String,
    @SerializedName("country_capital")
    val countryCapital: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("country_code_iso3")
    val countryCodeIso3: String,
    @SerializedName("country_name")
    val countryName: String,
    @SerializedName("country_population")
    val countryPopulation: Double,
    @SerializedName("country_tld")
    val countryTld: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("currency_name")
    val currencyName: String,
    @SerializedName("in_eu")
    val inEu: Boolean,
    @SerializedName("ip")
    val ip: String,
    @SerializedName("languages")
    val languages: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("org")
    val org: String,
    @SerializedName("postal")
    val postal: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("region_code")
    val regionCode: String,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("utc_offset")
    val utcOffset: String
)