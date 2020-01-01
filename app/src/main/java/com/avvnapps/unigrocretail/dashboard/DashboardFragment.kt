package com.avvnapps.unigrocretail.dashboard

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.dashboard.adapters.DashboardPagerAdapter
import com.avvnapps.unigrocretail.location_address.SavedAddressesActivity
import com.avvnapps.unigrocretail.utils.GpsUtils
import com.avvnapps.unigrocretail.utils.LocationUtils
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DashboardFragment : Fragment() {

    val TAG = "DASHBOARD_FRAG"
    val SET_ADDRESS_REQUEST_CODE = 100
    lateinit var location: Location
    lateinit var activity: AppCompatActivity

    lateinit var dashboardView: View
    private lateinit var gpsUtils: GpsUtils


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


        // get user location and pass to geocoder for address
        LocationUtils(activity).getLocation().observe(activity, Observer { loc: Location? ->
            if (loc != null) {
                location = loc!!
                //updateAddress()
                getLocation()
            }

        })

        dashboardView.appbar_dashboard_set_delivery_location_tv.setOnClickListener {
            var intent = Intent(activity, SavedAddressesActivity::class.java)
            intent.putExtra("is_selectable_action", true)
            startActivityForResult(intent, SET_ADDRESS_REQUEST_CODE)
        }

        fragment_dashboard_view_pager.adapter =
            DashboardPagerAdapter(childFragmentManager)
        fragment_dashboard_view_pager.offscreenPageLimit = 2

        appbar_dashboard_tab_layout.setupWithViewPager(fragment_dashboard_view_pager)

    }


    private fun updateAddress() {
        var address = LocationUtils.getAddress(activity, location.latitude, location.longitude)
        if (address != null) {
            Log.i(TAG, address)
            dashboardView.appbar_dashboard_set_delivery_location_tv.text = address
        }
    }

    private fun getLocation() {
        gpsUtils.getLatLong { lat, long ->
            Log.i(TAG, "location is $lat + $long")

            var address = LocationUtils.getAddress(activity, lat, long)
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
                location.latitude = data!!.extras.getDouble("latitude")
                location.longitude = data!!.extras.getDouble("longitude")
                updateAddress()
            }
        }
    }

}