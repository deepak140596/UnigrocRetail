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

        logOutTv.setOnClickListener {
            logOutAlert()
        }
    }

    fun logOutAlert(){
        val builder = AlertDialog.Builder(activity)
        //set title for alert dialog
        builder.setTitle("Log out")
        //set message for alert dialog
        builder.setMessage("Do you want to log out?")
        builder.setIcon(R.drawable.ic_logout)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            signOut()
        }

        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun signOut() {
        AuthUI.getInstance()
            .signOut(activity)
            .addOnCompleteListener {
                val intent = Intent(activity, AuthUiActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
    }


}
