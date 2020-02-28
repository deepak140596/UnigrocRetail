package com.avvnapps.unigrocretail.account_settings


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.authentication.AuthUiActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account.*


class Account : Fragment() {
    lateinit var activity: AppCompatActivity

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
        var user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            userNametv.setText(user.displayName)
        }
        setupClickListener()


    }

    private fun setupClickListener() {

        change_password.setOnClickListener {
            val intent = Intent(activity, ChangePassword::class.java)
            startActivity(intent)
        }

        logOutTv.setOnClickListener {
            logOutAlert()
        }
    }

    private fun logOutAlert(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Log out")
        builder.setMessage("Do you want to log out?")
        builder.setIcon(R.drawable.ic_logout)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            signOut()
        }

        builder.setNegativeButton("No"){dialogInterface, which ->
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
