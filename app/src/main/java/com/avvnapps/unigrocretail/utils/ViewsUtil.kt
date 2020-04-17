package com.avvnapps.unigrocretail.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import es.dmoral.toasty.Toasty

//Success Toast
fun Context.successToast(message: String) =
    Toasty.success(this, message, Toast.LENGTH_SHORT).show()

//Error Toast
fun Context.errorToast(message: String) =
    Toasty.error(this, message, Toast.LENGTH_SHORT).show()

//Progress Bar
fun setProgressDialog(context: Context, message:String):AlertDialog {
    val llPadding = 16
    val ll = LinearLayout(context)
    ll.orientation = LinearLayout.HORIZONTAL
    ll.setPadding(llPadding, llPadding, llPadding, llPadding)
    ll.gravity = Gravity.CENTER
    var llParam = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT)
    llParam.gravity = Gravity.CENTER
    ll.layoutParams = llParam

    val progressBar = ProgressBar(context)
    progressBar.isIndeterminate = true
    progressBar.setPadding(0, 0, llPadding, 0)
    progressBar.layoutParams = llParam

    llParam = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT)
    llParam.gravity = Gravity.CENTER
    val tvText = TextView(context)
    tvText.text = message
    tvText.setTextColor(Color.parseColor("#000000"))
    tvText.textSize = 20.toFloat()
    tvText.layoutParams = llParam

    ll.addView(progressBar)
    ll.addView(tvText)

    val builder = AlertDialog.Builder(context)
    builder.setCancelable(false)
    builder.setView(ll)

    val dialog = builder.create()
    val window = dialog.window
    if (window != null) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
    }
    return dialog
}

fun circularProgressDrawable(context: Context): CircularProgressDrawable {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()

    return circularProgressDrawable
}
