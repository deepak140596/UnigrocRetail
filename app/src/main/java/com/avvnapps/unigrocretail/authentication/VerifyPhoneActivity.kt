package com.avvnapps.unigrocretail.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_verify_phone.*
import java.util.concurrent.TimeUnit
import android.content.Intent
import com.avvnapps.unigrocretail.MainActivity
import com.avvnapps.unigrocretail.NavigationActivity
import com.avvnapps.unigrocretail.R


/**
 * Created by Deepak Prasad on 11-02-2019.
 */

class VerifyPhoneActivity : AppCompatActivity() {

    private val TAG = "VERIFY_PHONE"
    var currentStep = 0

    lateinit var getOtpCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        title = "Verify Phone"


        phone_input_layout.setDefault()


        step_view.setStepsNumber(3)
        step_view.go(0, true)

        initializeOtpCallback()


        // send OTP btn
        activity_send_otp_btn.setOnClickListener {
            sendOtpAction()
        }

        // didn't get code
        activity_verify_didnt_get_code.setOnClickListener {
            Toasty.info(this@VerifyPhoneActivity, "Verification Code Resent!", Toast.LENGTH_SHORT)
                .show()
            sendOtp(phone_input_layout.number) // resend code
        }

        // activity_verify_otp_btn
        activity_verify_otp_btn.setOnClickListener {
            var code = activity_verify_pinView.text.toString()
            verifyPhoneNumberWithCode(storedVerificationId, code)
        }

        activity_verify_done_btn.setOnClickListener {
            startActivity(Intent(this@VerifyPhoneActivity, RetailerAddInfo::class.java))
            finish()
        }


    }

    fun sendOtpAction() {
        var valid = true


        if (!phone_input_layout.isValid) {
            Toasty.error(this, getString(R.string.invalid_phone_number)).show()
            valid = false
        }

        if (valid) {

            Log.d(TAG, "Phone Number: " + phone_input_layout.phoneNumber)
            incrementStepCount()
            sendOtp(phone_input_layout.number)

            // setup UI
            activity_verify_entered_phone_num_tv.text = phone_input_layout.number
            layout1.visibility = View.GONE
            layout2.visibility = View.VISIBLE
            activity_verify_pinView.setText("")
            activity_verify_pinView.requestFocus()

        } else
            Toast.makeText(this, R.string.invalid_phone_number, Toast.LENGTH_SHORT).show()
    }

    fun verifiedOtpAction() {
        Toasty.success(this@VerifyPhoneActivity, "Phone Verified!", Toast.LENGTH_SHORT).show()
        layout1.visibility = View.GONE
        layout2.visibility = View.GONE
        layout3.visibility = View.VISIBLE
        incrementStepCount()
        activity_verify_done_btn.requestFocus()
    }

    fun sendOtp(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            getOtpCallback
        )
        activity_verify_pinView.setText("")
    }

    fun incrementStepCount() {
        if (currentStep < step_view.stepCount - 1) {
            currentStep++
            step_view.go(currentStep, true)
        } else
            step_view.done(true)
    }

    fun gotoStepOne() {
        layout1.visibility = View.VISIBLE
        layout2.visibility = View.GONE
        layout3.visibility = View.GONE
        step_view.go(0, true)
        currentStep = 0
    }

    fun verifyPhoneNumberWithCode(verificationID: String?, code: String) {
        Log.i(TAG, "code: " + code)
        try {
            val credential = PhoneAuthProvider.getCredential(verificationID!!, code)
            Log.i(TAG, "Credential: " + credential)
            linkPhoneAuthWithCredential(credential)
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying")
        }


    }


    fun initializeOtpCallback() {
        getOtpCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "Phone Verified! $credential")
                //verifiedOtpAction()
                linkPhoneAuthWithCredential(credential)

            }

            override fun onVerificationFailed(p0: FirebaseException) {

                Log.e(TAG, "onVerificationFailed $p0")
                if (p0 is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toasty.error(this@VerifyPhoneActivity, "Invalid Phone Number. Try Again!")
                        .show()
                    gotoStepOne()
                } else if (p0 is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toasty.error(this@VerifyPhoneActivity, "Please try after some time!").show()
                }
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)

                Log.i(TAG, "VI: $p0")
                Log.i(TAG, "RT: $p1")
                storedVerificationId = p0
                resendToken = p1
            }

        }
    }

    fun linkPhoneAuthWithCredential(credential: PhoneAuthCredential) {
        activity_verify_progress_bar.visibility = View.VISIBLE
        var auth = FirebaseAuth.getInstance()
        auth.currentUser!!.linkWithCredential(credential)
            .addOnSuccessListener {
                verifiedOtpAction()
                activity_verify_progress_bar.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to verify Phone! + ${it.message}")
                gotoStepOne()
                activity_verify_progress_bar.visibility = View.GONE
                Toasty.error(
                    this@VerifyPhoneActivity,
                    "Verification Failed! Try Again!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}