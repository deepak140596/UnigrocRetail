package com.avvnapps.unigrocretail.account_settings


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.authentication.AuthUiActivity
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.store_reviews.ReviewsActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.Slider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.location_range_bottomsheet.view.*
import java.text.NumberFormat


class Account : Fragment() {
    lateinit var activity: AppCompatActivity

    val user by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity = getActivity() as AppCompatActivity

        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        userNametv.text =
            user?.displayName ?: throw IllegalArgumentException("not found name")

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
            .into(profileCircleImageView)


        setupClickListener()


    }

    private fun setupClickListener() {

        profile_details_rl.setOnClickListener {
            val intent = Intent(activity, ShopProfile::class.java)
            startActivity(intent)
        }

        edit_profile_tv.setOnClickListener {
            val intent = Intent(activity, EditProfile::class.java)
            startActivity(intent)
        }

        change_password.setOnClickListener {
            val intent = Intent(activity, ChangePassword::class.java)
            startActivity(intent)
        }

        logOutTv.setOnClickListener {
            logOutAlert()
        }

        change_range.setOnClickListener {
            setLocationRange()
        }

        store_reviews.setOnClickListener {
            val intent = Intent(activity, ReviewsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setLocationRange() {
        val dialog = BottomSheetDialog(activity)
        val view = layoutInflater.inflate(R.layout.location_range_bottomsheet, null)

        val minDistl = SharedPreferencesDB.getLocationRange(activity)
        if (minDistl == -1) {
        } else {
            view.sliderLocation.value = minDistl.toFloat()
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            view.locationRangeTv.text =
                "Delivery Range : ${format.format(minDistl)} km"
        }

        view.sliderLocation.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
                val format = NumberFormat.getInstance()
                format.maximumFractionDigits = 0
                view.locationRangeTv.text =
                    "Delivery Range : ${format.format(slider.value.toDouble())} km"
                SharedPreferencesDB.saveLocationRange(
                    activity,
                    format.format(slider.value.toDouble()).toDouble()
                )

            }
        })

        view.sliderLocation.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            format.format(value.toDouble())
        }

        view.sliderLocation.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            view.locationRangeTv.text = "Delivery Range ${format.format(value.toDouble())}km"
        }
       

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun logOutAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Log out")
        builder.setMessage("Do you want to log out?")
        builder.setIcon(R.drawable.ic_logout)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            signOut()
        }

        builder.setNegativeButton("No") { dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(activity)
            .addOnCompleteListener {
                val intent = Intent(activity, AuthUiActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
    }


}
