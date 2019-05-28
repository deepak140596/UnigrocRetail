package com.avvnapps.unigrocretail

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.avvnapps.unigrocretail.dashboard.DashboardFragment
import com.avvnapps.unigrocretail.utils.LocationUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity() {

    var TAG = "NAV_ACTIVITY"
    lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        askForPermissions()

        activity_bottom_nav_view.setOnNavigationItemSelectedListener(mOnBottomNavigationItemSelectedListener)

        var user = FirebaseAuth.getInstance().currentUser
        Log.i(TAG,"Name: ${user!!.displayName}  Email: ${user!!.email}  Phone: ${user.phoneNumber}")
    }



    private val mOnBottomNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {

        when(it.itemId){
            R.id.bottom_navigation_dashboard ->{
                //startActivity(Intent(this@NavigationActivity,SavedAddressesActivity::class.java))
                startFragment(DashboardFragment())
            }

            R.id.bottom_navigation_account ->
                Toasty.info(this@NavigationActivity,"Account!").show()
        }

        return@OnNavigationItemSelectedListener true
    }

    fun startFragment(fragment : Fragment){
        if(fragment != null){
            var fragmentManager =supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.activity_nav_frame_layout,fragment,"").commit()
        }
    }


    // observe on location and update accordingly
    fun updateLocation(){

        LocationUtils(this).getLocation().observe(this, Observer { loc: Location? ->


            if(loc != null) {
                location = loc!!
                Log.i(TAG,"Location: ${location.latitude}  ${location.longitude}")
                startFragment(DashboardFragment())
            }
        })
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

                    updateLocation()


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
}
