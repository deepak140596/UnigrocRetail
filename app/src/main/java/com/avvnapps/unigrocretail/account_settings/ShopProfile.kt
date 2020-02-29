package com.avvnapps.unigrocretail.account_settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.models.UserInfo
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_shop_profile.*

class ShopProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_profile)
        var user = FirebaseAuth.getInstance().currentUser

        var userinfo = SharedPreferencesDB.getSavedUser(this)
        shop_Name_tv.text = userinfo?.shopName.toString()
        owner_name_tv.text = user?.displayName.toString()
        email_view_tv.text = user?.email.toString()
        phone_no_view_tv.text = user?.phoneNumber.toString()
        Glide.with(this)
            .load(user?.photoUrl)
            .into(profile_pic)

    }

}
