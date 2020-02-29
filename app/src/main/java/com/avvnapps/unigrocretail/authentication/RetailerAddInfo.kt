package com.avvnapps.unigrocretail.authentication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.avvnapps.unigrocretail.NavigationActivity
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.models.UserInfo
import com.avvnapps.unigrocretail.utils.setProgressDialog
import com.avvnapps.unigrocretail.viewmodel.FirestoreViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.android.synthetic.main.activity_retailer_add_info.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File


class RetailerAddInfo : AppCompatActivity() {
    val TAG = "RETAILERINFO"

    var IMAGE_STATUS = false
    private var compressedImage: File? = null
    private var thumb_image: Bitmap? = null
    var user = FirebaseAuth.getInstance().currentUser!!
    var firestoreViewModel: FirestoreViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retailer_add_info)
        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel::class.java)

        setupClickListener()
    }

    private fun setupClickListener() {
        selectProfilePic.setOnClickListener(View.OnClickListener { view ->
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) { // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) { // do you work now
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.type = "image/*"
                            startActivityForResult(intent, 1000)
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) { // permission is denied permenantly, navigate user to app settings
                            Snackbar.make(
                                view,
                                "Kindly grant Required Permission",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("Allow", null).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                })
                .onSameThread()
                .check()
            //result will be available in onActivityResult which is overridden
        })
        register_button.setOnClickListener {

            val shopName = shop_name.text.toString().trim()

            if (Validate(shopName) && validateProfile()) {
                uploadImageAndSaveUri(shopName, thumb_image)
            }

        }
    }

    private fun uploadImageAndSaveUri(
        shopName: String,
        thumbImage: Bitmap?
    ) {
        val dialog = setProgressDialog(this, "Please wait..")
        dialog.show()
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("shopPics/${FirebaseAuth.getInstance().currentUser?.uid}")
        thumbImage?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)

        upload.addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    var profile_pic = urlTask.result.toString()
                    var userValues = UserInfo(
                        user.displayName.toString(),
                        user.email.toString(),
                        user.phoneNumber.toString(),
                        profile_pic,
                        shopName
                    )
                    firestoreViewModel?.saveUserData(userValues)

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(profile_pic))
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                SharedPreferencesDB.savePreferredUser(this, userValues)
                                dialog.dismiss()

                                val intent = Intent(this, NavigationActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                Log.d(TAG, "User profile updated.")
                            } else {
                                Log.d(TAG, "User profile is not updated.")

                            }
                        }

                }
            } else {
                uploadTask.exception?.let {
                    showError(it.message!!)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!")
                return
            }
            val imageUri = data.data //Geting uri of the data
            IMAGE_STATUS = true //setting the flag
            CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .setMinCropWindowSize(500, 500)
                .start(this)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {

                val thumb_filePath = File(result.uri.path)

                thumb_filePath.let { imageFile ->
                    lifecycleScope.launch {
                        // Full custom
                        compressedImage = Compressor.compress(this@RetailerAddInfo, imageFile) {
                            resolution(200, 200)
                            quality(75)
                            format(Bitmap.CompressFormat.WEBP)
                        }
                        compressedImage?.let {
                            thumb_image = BitmapFactory.decodeFile(it.absolutePath)
                            profilePic.setImageBitmap(thumb_image)
                        }

                    }
                } ?: showError("Please choose an image!")


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // showError(result.error)
            }
        }

    }

    private fun showError(errorMessage: String) {
        Toasty.error(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun validateProfile(): Boolean {
        if (!IMAGE_STATUS) {
            Toasty.info(
                this,
                "Select A Shop Picture",
                Toast.LENGTH_LONG
            ).show()
        }
        return IMAGE_STATUS
    }

    private fun Validate(shopName: String): Boolean {
        var check = true
        if (check) {
            if (shopName.isEmpty()) {
                shop_name.setError("Cannot Be Empty")
                check = false
            }
        } else {
            check = true
        }
        return check
    }
}
