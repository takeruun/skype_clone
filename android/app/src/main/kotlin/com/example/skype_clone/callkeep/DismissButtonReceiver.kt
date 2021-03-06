package com.example.skype_clone.callkeep

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

private const val TAG = "DismissButtonReceiver"

@RequiresApi(Build.VERSION_CODES.M)
class DismissButtonReceiver: BroadcastReceiver() {
    private var notifyManager: NotificationManager? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Notification Dialog Dismiss")

        val notificationChannelId = intent!!.extras?.getInt("NOTIFICATION_ID")
        notifyManager = context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notifyManager!!.cancelAll()

        val conn = CallConnectionService.getConnection("019100-192819") ?: return
        conn.onDisconnect()
    }
}