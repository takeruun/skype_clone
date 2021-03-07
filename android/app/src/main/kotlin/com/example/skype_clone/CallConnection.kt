package com.example.skype_clone

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.Context
import android.content.Intent
import android.telecom.*
import android.telecom.CallAudioState
import android.telecom.Connection
import android.util.Log
import io.wazo.callkeep.IncomingCallActivity

class CallConnection(ctx: Context) : Connection() {

  var ctx: Context = ctx
  val TAG = "CallConnection"

  init {}

  override fun onShowIncomingCallUi() {
    Log.i(TAG, "onShowIncomingCallUi")
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
    intent.setClass(ctx, IncomingCallActivity::class.java!!)
    val pendingIntent = PendingIntent.getActivity(ctx, 1, intent, 0)
    val builder = Notification.Builder(ctx)
    builder.setOngoing(true)
    builder.setPriority(Notification.PRIORITY_HIGH)

    // Set notification content intent to take user to fullscreen UI if user taps on the
    // notification body.
    builder.setContentIntent(pendingIntent)
    // Set full screen intent to trigger display of the fullscreen UI when the notification
    // manager deems it appropriate.
    builder.setFullScreenIntent(pendingIntent, true)

    // Setup notification content.
    builder.setSmallIcon(R.mipmap.ic_launcher)
    builder.setContentTitle("Your notification title")
    builder.setContentText("Your notification content.")

    // Use builder.addAction(..) to add buttons to answer or reject the call.

    val notificationManager = ctx.getSystemService(NotificationManager::class.java)

    notificationManager.notify("Call Notification", 37, builder.build())
  }

  override fun onCallAudioStateChanged(state: CallAudioState?) {
    Log.i(TAG, "onCallAudioStateChanged")
  }

  override fun onAnswer() {
    Log.i(TAG, "onAnswer")
  }

  override fun onDisconnect() {
    Log.i(TAG, "onDisconnect")
  }

  override fun onHold() {
    Log.i(TAG, "onHold")
  }

  override fun onUnhold() {
    Log.i(TAG, "onUnhold")
  }

  override fun onReject() {
    Log.i(TAG, "onReject")
  }
}
