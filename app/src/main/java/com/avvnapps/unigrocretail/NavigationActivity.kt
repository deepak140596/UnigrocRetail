package com.avvnapps.unigrocretail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.avvnapps.unigrocretail.utils.GpsUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_navigation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NavigationActivity : AppCompatActivity() {

    var TAG = "NAV_ACTIVITY"

    private val gpsUtils by lazy { GpsUtils(this) }
    val firestoreDB: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val user by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        askForPermissions()

        activity_bottom_nav_view.setOnItemSelectedListener { id ->
            when (id) {
                R.id.bottom_navigation_dashboard -> {
                    //startActivity(Intent(this@NavigationActivity,SavedAddressesActivity::class.java))
                    startFragment(DashboardFragment())
                }
                R.id.bottom_navigation_search ->{
                    //  startActivity(Intent(this@NavigationActivity,RetailerAddInfo::class.java))

                    Toasty.info(this@NavigationActivity, "Search!").show()
                }
                R.id.bottom_navigation_account ->{
                    startFragment(Account())
                }

                else -> {
                    //startActivity(Intent(this@NavigationActivity,SavedAddressesActivity::class.java))
                    startFragment(DashboardFragment())
                }
            }
        }

        getUserData()

        // startFragment(DashboardFragment())
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
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            1 ->{

                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted
                    //isPermissionAcquired = true
                   // updateLocation()
                    getLocation()
                }
                else{
                    // permission was denied
                    // isPermissionAcquired = false
                    Toasty.error(this,"Permission Denied").show()
                    // set empty list view
                }
            }

        }
    }

    private fun getLocation() {
        activity_bottom_nav_view.setItemSelected(
            R.id.bottom_navigation_dashboard,
            true
        )
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
