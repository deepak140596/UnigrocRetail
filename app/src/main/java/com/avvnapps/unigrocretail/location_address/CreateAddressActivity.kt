package com.avvnapps.unigrocretail.location_address

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.models.AddressItem
import com.avvnapps.unigrocretail.utils.GpsUtils
import com.avvnapps.unigrocretail.viewmodel.FirestoreViewModel
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_create_address.*

class CreateAddressActivity : AppCompatActivity() {
    val TAG = "CREATE_ADDRESS"
    private val PLACE_PICKER_REQUEST = 1
    var latitutde: Double? = null
    var longitude: Double? = null
    val firestoreViewModel by lazy { ViewModelProvider(this).get(FirestoreViewModel::class.java) }
    private var addressItem: AddressItem? = null

    private val gpsUtils by lazy { GpsUtils(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_address)

        val data = intent.getSerializableExtra("address_item")
        if (data != null)
            addressItem = data as AddressItem
        if(addressItem != null){
            setupViews()
        }
        getLocation()

        create_address_set_location_btn.setOnClickListener {
            //createPlacePicker()
            getLocation()
        }

        create_address_done_fab.setOnClickListener {
            if(isFormValid()){
                val addressItem = AddressItem(
                    create_address_name_edit_text.text.toString(),
                    create_address_name_edit_text.text.toString(),
                    create_address_house_name_edit_text.text.toString(),
                    create_address_locality_edit_text.text.toString(),
                    create_address_landmark_edit_text.text.toString(),
                    latitutde!!, longitude!!
                )

                firestoreViewModel.saveAddressToFirebase(addressItem)
                // save the selected address as default
                SharedPreferencesDB.savePreferredAddress(this@CreateAddressActivity,addressItem)
                finish()
            }
        }

    }

    private fun getLocation() {


        gpsUtils.getLatLong { lat, long ->
            Log.i(TAG, "location is $lat + $long")
            latitutde = lat
            longitude = long

            create_address_is_location_set_cb.isChecked = true
        }
    }

    private fun setupViews() {
        create_address_house_name_edit_text.setText(addressItem!!.houseName)
        create_address_locality_edit_text.setText(addressItem!!.locality)
        create_address_landmark_edit_text.setText(addressItem!!.landmark)
        create_address_name_edit_text.setText(addressItem!!.addressName)
        latitutde = addressItem!!.latitude
        longitude = addressItem!!.longitude
        create_address_is_location_set_cb.isChecked = true
    }

    private fun isFormValid(): Boolean {

        if (create_address_house_name_edit_text.text.toString().trim().isEmpty()) {
            create_address_house_name_input_layout.error = "Enter House Name"
            return false
        }
        if (create_address_locality_edit_text.text.toString().trim().isEmpty()) {
            create_address_locality_input_layout.error = "Enter Locality"
            return false
        }
        if (create_address_landmark_edit_text.text.toString().trim().isEmpty()) {
            create_address_landmark_input_layout.error = "Enter Landmark"
            return false
        }
        if (create_address_name_edit_text.text.toString().trim().isEmpty()) {
            create_address_name_input_layout.error = "Save address as"
            return false
        }
        if (latitutde == null || longitude == null) {
            Toasty.error(this, "Please select location on Map!").show()
            return false
        }
        return true

    }

    fun createPlacePicker(){

        val builder = PlacePicker.IntentBuilder()


        try {
            Log.d(TAG, "opening startActivityforResult")
            startActivityForResult(builder.build(this@CreateAddressActivity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }

    }


    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                val place = PlacePicker.getPlace(this,data)
                val latLng = place.latLng
                Log.d(TAG, "LatLng: $latLng")
                latitutde = latLng.latitude
                longitude = latLng.longitude

                create_address_is_location_set_cb.isChecked = true
            }
        }
    }
}
