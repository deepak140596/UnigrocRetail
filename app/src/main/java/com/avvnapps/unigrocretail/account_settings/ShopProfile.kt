package com.avvnapps.unigrocretail.account_settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_shop_profile.*


class ShopProfile : AppCompatActivity() {

    val TAG = "ShopProfile"
    val user by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_profile)


        val userInfo = SharedPreferencesDB.getSavedUser(this)
        shop_Name_tv.text = userInfo?.shopName.toString()
        owner_name_tv.text = user?.displayName.toString()
        email_view_tv.text = user?.email.toString()
        phone_no_view_tv.text = user?.phoneNumber.toString()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.user_placeholder)
            .error(R.drawable.user_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform()

        Glide.with(this)
            .applyDefaultRequestOptions(options)
            .load(user?.photoUrl)
            .into(profile_pic)

    }

}
