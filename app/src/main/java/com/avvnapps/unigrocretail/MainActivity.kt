package com.avvnapps.unigrocretail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.avvnapps.unigrocretail.location_address.SavedAddressesActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_tv.setOnClickListener {
            startActivity(Intent(this@MainActivity,SavedAddressesActivity::class.java))
        }
    }
}
