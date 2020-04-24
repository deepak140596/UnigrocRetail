package com.avvnapps.unigrocretail

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.avvnapps.unigrocretail.authentication.AuthUiActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    val TAG = "SPLASH_SCREEN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firestore = Firebase.firestore

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
            Runnable /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */
            {
                // This method will be executed once the timer is over
                // Start your app main activity
                val auth = FirebaseAuth.getInstance()
                if (auth.currentUser != null) {

                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w(TAG, "getInstanceId failed", task.exception)
                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            val token = task.result?.token.toString()
                            val data = hashMapOf("deviceToken" to token)

                            firestore.collection("retailers")
                                .document(auth.currentUser!!.email.toString())
                                .set(data, SetOptions.merge())
                            // Log and toast
                            Log.d(TAG, "tokenID $token")
                            // already signed in
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    NavigationActivity::class.java
                                )
                            )

                        })


                } else {
                    // not signed in
                    startActivity(Intent(this@SplashActivity, AuthUiActivity::class.java))

                }
                finish()
            }, 2500
        )
    }
}
