package com.medis.laboratcall.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import android.os.Build
import android.app.NotificationChannel
import android.annotation.TargetApi
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.medis.laboratcall.DetailPromo
import com.medis.laboratcall.Fragment.FragmentOffLocation
import com.medis.laboratcall.HomeActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager




internal class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        Log.d("NEW_TOKEN", s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
       // showNotification(remoteMessage!!.getNotification()!!.getBody())

        val intent = Intent("data_pemeriksaan")
        broadcaster!!.sendBroadcast(intent)

        val i = Intent("data_pemeriksaan_oncall")
        broadcaster!!.sendBroadcast(i)
    }

    private fun showNotification(message: String?) {
        val i = Intent(this, HomeActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)

        var mNotifyManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel(mNotifyManager)
        val mBuilder = NotificationCompat.Builder(this, "YOUR_TEXT_HERE")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Permintaan Pemeriksaan")
                .setContentText(message)
                .setContentIntent(pendingIntent)
            mNotifyManager.notify(0, mBuilder.build())

    }

    @TargetApi(26)
    private fun createChannel(notificationManager: NotificationManager) {
        val name = "FileDownload"
        val description = "Notifications for download status"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val mChannel = NotificationChannel(name, name, importance)
        mChannel.description = description
        mChannel.enableLights(true)
        mChannel.lightColor = Color.BLUE
        notificationManager.createNotificationChannel(mChannel)
    }


}