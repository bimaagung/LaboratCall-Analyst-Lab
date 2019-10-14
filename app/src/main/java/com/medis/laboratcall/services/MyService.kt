package com.medis.laboratcall.services

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import java.nio.file.Files.size
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.iid.FirebaseInstanceId





class MyService : FirebaseMessagingService() {

    override  fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                Toast.makeText(applicationContext, "Berhasil", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()    // Handle message within 10 seconds
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: " + token!!)
    }



}
