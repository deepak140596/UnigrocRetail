package com.avvnapps.unigrocretail.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.avvnapps.unigrocretail.models.AddressItem
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.lang.ref.WeakReference

class GpsUtils {

    private val GPS_REQUEST_CODE = 1001
    private var weakActivity: WeakReference<FragmentActivity>? = null
    private var weakFragment: WeakReference<Fragment>? = null

    private lateinit var settingsClient: SettingsClient
    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    constructor(fragment: Fragment) {
        weakFragment = WeakReference(fragment)
        init(fragment.requireActivity().applicationContext)
    }

    constructor(activity: FragmentActivity) {
        weakActivity = WeakReference(activity)
        init(activity.applicationContext)
    }

    private fun init(context: Context) {
        settingsClient = LocationServices.getSettingsClient(context)
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create()

        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = (10 * 1000).toLong()
            fastestInterval = (1 * 1000).toLong()
        }

        locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()
    }

    // ======= CALLBACKS =======
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations ?: return

            locationResult.locations.filterNotNull().forEach {
                locationResultCallback?.invoke(it.latitude, it.longitude)
                fusedLocationClient.removeLocationUpdates(this)
                onProgressUpdate?.invoke(false)
            }
        }
    }
    private var locationResultCallback: ((lat: Double, long: Double) -> Unit)? = null
    var onProgressUpdate: ((show: Boolean) -> Unit)? =
        null // getting location may take second or two

    private val gpsEnabledListener: (enabled: Boolean) -> Unit = {
        getLocation()
    }

    // check GPS state, if disabled open dialog and send back the result
    fun getLatLong(listener: (lat: Double, long: Double) -> Unit) {
        locationResultCallback = listener


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsEnabledListener(true)
        } else {

            fun checkSettings() {
                settingsClient
                    .checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener { onEnabledInSettings() }
                    .addOnFailureListener { onDisabledInSettings(it) }
            }

            weakActivity?.safeGet { checkSettings() }
            weakFragment?.safeGet { checkSettings() }
        }


    }


    // enabled in settings
    private fun onEnabledInSettings() {
        gpsEnabledListener(true)
    }

    // disabled in settings, prompt to enable with dialog, result will be sent to onActivityResult
    private fun onDisabledInSettings(e: Exception) {

        fun openSystemGpsEnablerDialog(fragment: Fragment, e: ResolvableApiException) {
            fragment.startIntentSenderForResult(
                e.resolution.intentSender,
                GPS_REQUEST_CODE,
                null,
                0,
                0,
                0,
                null
            )
        }

        fun openSystemGpsEnablerDialog(activity: FragmentActivity, e: ResolvableApiException) {
            e.startResolutionForResult(activity, GPS_REQUEST_CODE)
        }

        if (e !is ApiException) return
        when (e.statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                if (e !is ResolvableApiException) return

                weakActivity?.safeGet { openSystemGpsEnablerDialog(this, e) }
                weakFragment?.safeGet { openSystemGpsEnablerDialog(this, e) }
            }

            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                //Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings.
            }
        }
    }


    private fun getLocation() {

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            onProgressUpdate?.invoke(true)
            if (location != null) {
                locationResultCallback?.invoke(location.latitude, location.longitude)
            } else {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }

    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GPS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getLocation()
            } else {
                locationResultCallback?.invoke(-1.0, -1.0)
            }
        }
    }

    private fun <T> WeakReference<T>.safeGet(body: T.() -> Unit) {
        this.get()?.body()
    }


}