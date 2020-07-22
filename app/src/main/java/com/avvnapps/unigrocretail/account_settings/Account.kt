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
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account.*


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
