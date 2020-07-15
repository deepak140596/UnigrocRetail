package com.avvnapps.unigrocretail.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.dashboard.adapters.DashboardPagerAdapter
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.location_address.SavedAddressesActivity
import com.avvnapps.unigrocretail.utils.GpsUtils
import com.avvnapps.unigrocretail.utils.LocationUtils
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DashboardFragment : Fragment() {

    val TAG = "DASHBOARD_FRAG"
    val SET_ADDRESS_REQUEST_CODE = 100
    lateinit var activity: AppCompatActivity

    lateinit var dashboardView: View
    private lateinit var gpsUtils: GpsUtils

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = getActivity() as AppCompatActivity
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gpsUtils = GpsUtils(this)

        dashboardView = view
        activity.setSupportActionBar(appbar_dashboard_toolbar)
        dashboardView = appbar_dashboard_toolbar.rootView

        dashboardView.appbar_dashboard_set_delivery_location_tv.setOnClickListener {
            val intent = Intent(activity, SavedAddressesActivity::class.java)
            intent.putExtra("is_selectable_action", true)
            startActivityForResult(intent, SET_ADDRESS_REQUEST_CODE)
        }

        fragment_dashboard_view_pager.apply {
            adapter = DashboardPagerAdapter(childFragmentManager)
            offscreenPageLimit = 2
        }

        appbar_dashboard_tab_layout.setupWithViewPager(fragment_dashboard_view_pager)
        updateAddress()
    }


    // observe on location and update accordingly
    private fun updateAddress() {

        val addressData = SharedPreferencesDB.getSavedAddress(activity)

        if (latitude == 0.0 && longitude == 0.0) {

            if (addressData == null) {
                dashboardView.appbar_dashboard_set_delivery_location_tv.text = "Select Address"
            } else {
                val address =
                    LocationUtils.getAddress(activity, addressData.latitude, addressData.longitude)
                if (address != null) {
                    Log.i(TAG, address)
                    dashboardView.appbar_dashboard_set_delivery_location_tv.text = address
                }
            }

        } else {
            val address = LocationUtils.getAddress(activity, latitude, longitude)
            if (address != null) {
                Log.i(TAG, address)
                dashboardView.appbar_dashboard_set_delivery_location_tv.text = address
            }
        }
    }


    private fun getLocation() {
        gpsUtils.getLatLong { lat, long ->
            Log.i(TAG, "location is $lat + $long")

            val address = LocationUtils.getAddress(activity, lat, long)
            if (address != null) {
                Log.i(TAG, address)
                appbar_dashboard_set_delivery_location_tv.text = address
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SET_ADDRESS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                latitude = data!!.extras!!.getDouble("latitude")
                longitude = data.extras!!.getDouble("longitude")
                updateAddress()
            }
        }
    }

}