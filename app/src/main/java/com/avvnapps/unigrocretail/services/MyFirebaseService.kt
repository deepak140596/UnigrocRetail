package com.avvnapps.unigrocretail.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.avvnapps.unigrocretail.NavigationActivity
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.UserInfo
import com.avvnapps.unigrocretail.worker.MyWorker
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseService"
    val user = FirebaseAuth.getInstance().currentUser.let { UserInfo(it!!) }

    val firestore = Firebase.firestore
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // handle a notification payload.
        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            sendNotification(
                remoteMessage.notification!!.title,
                remoteMessage.notification!!.body,
                remoteMessage.notification!!.icon
            )

            if (true) {
                scheduleJob()
            } else {
                handleNow()
            }
        }
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)

        }
    }


    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // sending reg id to your server
        sendRegistrationToServer(token)

    }

    private fun sendRegistrationToServer(token: String?): Task<Void> {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")

        user.deviceToken = token

        return firestore.runTransaction { transaction ->
            val userRef = firestore.collection("retailers")
                .document(user.email!!)

            transaction.set(userRef, user, SetOptions.merge())
            null
        }
    }


    private fun sendNotification(
        title: String?,
        body: String?,
        icon: String?
    ) {
        val intent = Intent(this, NavigationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.project_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.notification
                )
            )
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager!!.createNotificationChannel(channel)
        }

        notificationManager!!.notify(0, notificationBuilder.build())
    }

}