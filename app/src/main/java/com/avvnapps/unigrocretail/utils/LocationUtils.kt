package com.avvnapps.unigrocretail.utils

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.models.AddressItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.io.IOException
import java.util.*

class LocationUtils(context: Context) {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var location: MutableLiveData<Location> = MutableLiveData()

    // call constructor to get location
    init {
        getInstance(context)
        getLocation()
    }

    // using singleton pattern to get the locationProviderClient
    fun getInstance(appContext: Context): FusedLocationProviderClient {
        if (fusedLocationProviderClient == null)
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(appContext)
        return fusedLocationProviderClient!!
    }

    var onProgressUpdate: ((show: Boolean) -> Unit)? =
        null // getting location may take second or two

    fun getLocation(): LiveData<Location> {
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { loc: Location? ->
            onProgressUpdate?.invoke(true)
            location.value = loc!!
        }

        return location
    }


    companion object {
        fun getAddress(activity: AppCompatActivity, lat: Double, lng: Double): String? {

            //Log.d(TAG, "get Address for LAT: $lat  LON: $lng")
            if (lat == 0.0 && lng == 0.0)
                return null
            val geocoder = Geocoder(activity, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val obj = addresses[0]
                var add = if (obj.thoroughfare == null) "" else obj.thoroughfare + ", "

                add += if (obj.subLocality == null) "" else obj.subLocality + ", "
                add += if (obj.subAdminArea == null) "" else obj.subAdminArea


                //Log.v("aTAG", "Address received: $add")

                return add
            } catch (e: IOException) {

                e.printStackTrace()
            }

            return ""
        }

        fun isNear(activity: AppCompatActivity, addressItem: AddressItem): Boolean {
            val minDist = 3
            if (getDistance(activity, addressItem) <= minDist)
                return true
            return false
        }

        private fun getDistance(activity: AppCompatActivity, addressItem: AddressItem): Float {
            var loc1 = Location("Location1")
            loc1.longitude = addressItem.longitude
            loc1.latitude = addressItem.latitude

            var loc2 = Location("Location2")
            val geoipVal = SharedPreferencesDB.getSavedGeoIp(activity)

            if (geoipVal != null) {
                loc2.longitude = geoipVal.longitude
            }

            if (geoipVal != null) {
                loc2.latitude = geoipVal.latitude
            }


            val fromPosition = LatLng(loc1.longitude, loc1.latitude)
            val toPosition = LatLng(loc2.longitude, loc2.latitude)
            val distance = SphericalUtil.computeDistanceBetween(fromPosition, toPosition)
            Log.v(
                "aTAG",
                "Address distant :" + loc1.distanceTo(loc2) + " Location2" + loc2.longitude + " " + loc2.latitude + " Location1" + loc1.longitude + " " + loc1.latitude
            )
            return loc1.distanceTo(loc2)

        }


    }

}