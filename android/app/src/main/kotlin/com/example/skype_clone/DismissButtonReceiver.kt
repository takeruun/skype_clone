package com.example.skype_clone.call_service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.skype_clone.call_service.CallConnectionService

@RequiresApi(Build.VERSION_CODES.M)
class DismissButtonReceiver: BroadcastReceiver() {
    private val TAG = "DismissButtonReceiver"
    private var notifManager: NotificationManager? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Notification Dialog Dismiss")

        val notificationChannelId = intent!!.extras?.getInt("NOTIFICATION_ID")
        notifManager = context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notifManager!!.cancel(notificationChannelId!!)

        val conn = CallConnectionService.getConnection("019100-192819") ?: return
        conn.onDisconnect()
    }
}