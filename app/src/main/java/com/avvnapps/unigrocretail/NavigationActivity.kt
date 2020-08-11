package com.avvnapps.unigrocretail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.avvnapps.unigrocretail.account_settings.Account
import com.avvnapps.unigrocretail.dashboard.DashboardFragment
import com.avvnapps.unigrocretail.database.GeoIpServicesManager.geoIpService
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.models.GeoIp
import com.avvnapps.unigrocretail.models.GeoIpResponseModel
import com.avvnapps.unigrocretail.models.UserInfo
import com.avvnapps.unigrocretail.order_history.OrderHistoryFragment
import com.avvnapps.unigrocretail.utils.GpsUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.skydoves.androidbottombar.BottomMenuItem
import com.skydoves.androidbottombar.OnMenuItemSelectedListener
import com.skydoves.androidbottombar.animations.BadgeAnimation
import com.skydoves.androidbottombar.forms.badgeForm
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_navigation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NavigationActivity : AppCompatActivity() {

    val TAG by lazy { "NAV_ACTIVITY" }

    private val gpsUtils by lazy { GpsUtils(this) }
    private val firestoreDB: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val user by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        askForPermissions()

        bottomBarView()

        getUserData()

        // startFragment(DashboardFragment())
    }

    private fun bottomBarView() {
        val badgeForm = badgeForm(this) {
            setBadgeTextSize(9f)
            setBadgePaddingLeft(6)
            setBadgePaddingRight(6)
            setBadgeDuration(550)
        }

        activity_bottom_nav_view.addBottomMenuItems(
            mutableListOf(
                BottomMenuItem(this)
                    .setTitle("Home")
                    .setBadgeForm(badgeForm)
                    .setBadgeColorRes(R.color.md_blue_200)
                    .setBadgeAnimation(BadgeAnimation.FADE)
                    .setBadgeText("New!")
                    .setIcon(R.drawable.ic_home)
                    .build(),

                BottomMenuItem(this)
                    .setTitle("Order History")
                    .setIcon(R.drawable.cart)
                    .build(),

                BottomMenuItem(this)
                    .setTitle("Account")
                    .setIcon(R.mipmap.ic_person_black)
                    .build()
            )


        )

        activity_bottom_nav_view.onMenuItemSelectedListener = object : OnMenuItemSelectedListener {
            override fun onMenuItemSelected(
                index: Int,
                bottomMenuItem: BottomMenuItem,
                fromUser: Boolean
            ) {
                when (index) {
                    0 -> {
                        activity_bottom_nav_view.dismissBadge(index)
                        startFragment(DashboardFragment())
                    }
                    1 -> {
                        activity_bottom_nav_view.dismissBadge(index)
                        startFragment(OrderHistoryFragment())
                    }
                    2 -> {
                        startFragment(Account())
                    }
                    else -> {
                        startFragment(DashboardFragment())
                    }
                }
            }
        }

        activity_bottom_nav_view.setOnBottomMenuInitializedListener {
            activity_bottom_nav_view.dismissBadge(1)

            // show badges after 1500 milliseconds.
            Handler().postDelayed({
                activity_bottom_nav_view.showBadge(index = 0)
                activity_bottom_nav_view.showBadge(1)
            }, 1500L)

        }

    }

    private fun getUserData() {
        val docRef = firestoreDB.collection("retailers").document(user?.email.toString())

        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.SERVER
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                val userValues = UserInfo(
                    user?.displayName.toString(),
                    user?.email.toString(),
                    user?.phoneNumber.toString(),
                    document?.get("profilePic").toString(),
                    document?.get("shopName").toString(),
                    document?.get("deviceToken").toString()
                )
                SharedPreferencesDB.savePreferredUser(this, userValues)


            } else {
                Log.d(TAG, "Cached get failed: ", task.exception)
            }
        }
    }


    private fun startFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.activity_nav_frame_layout, fragment, "")
            .commit()
    }


    private fun askForPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    //isPermissionAcquired = true
                    // updateLocation()
                    getLocation()
                } else {
                    // permission was denied
                    // isPermissionAcquired = false
                    Toasty.error(this, "Permission Denied").show()
                    // set empty list view
                }
            }

        }
    }

    private fun getLocation() {

        startFragment(DashboardFragment())


        gpsUtils.getLatLong { lat, long ->
            Log.i(TAG, "location is $lat + $long")
            // startFragment(DashboardFragment())
            val ipApiService = geoIpService
            ipApiService.getGeoIp().enqueue(object : Callback<GeoIpResponseModel?> {
                override fun onResponse(
                    call: Call<GeoIpResponseModel?>,
                    response: Response<GeoIpResponseModel?>
                ) {
                    try {
                        // Log.d(TAG, response.toString())
                        //Log.d(TAG, response.body().toString())
                        val countryName: String? = response.body()!!.countryName
                        val currency: String? = response.body()!!.currency
                        val country: String? = response.body()!!.country
                        val isp: String? = response.body()!!.ip
                        val GeoIpValues = GeoIp(
                            countryName!!,
                            currency!!,
                            country!!,
                            lat,
                            long,
                            isp.toString()
                        )

                        SharedPreferencesDB.savePreferredGeoIp(this@NavigationActivity, GeoIpValues)


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(call: Call<GeoIpResponseModel?>, t: Throwable) {
                    // Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.toString())
                }


            })

        }
    }
}
