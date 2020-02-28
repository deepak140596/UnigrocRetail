package com.avvnapps.unigrocretail.account_settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.utils.errorToast
import com.avvnapps.unigrocretail.utils.setProgressDialog
import com.avvnapps.unigrocretail.utils.successToast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        val dialog = setProgressDialog(this, "Please wait..")
        button_reset_password.setOnClickListener {
            val email = text_email.text.toString().trim()

            if (email.isEmpty()) {
                text_email.error = "Email Required"
                text_email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                text_email.error = "Valid Email Required"
                text_email.requestFocus()
                return@setOnClickListener
            }
            dialog.show()
            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    dialog.dismiss()
                    if (task.isSuccessful) {
                        this.successToast("Check your email")
                    } else {
                        this.errorToast(task.exception?.message!!)
                    }
                }
        }
    }
}
