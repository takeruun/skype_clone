package com.example.skype_clone.callkeep

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.skype_clone.R
import com.example.skype_clone.callkeep.Constants.ACTION_ANSWER_CALL
import com.example.skype_clone.callkeep.Constants.ACTION_AUDIO_SESSION
import com.example.skype_clone.callkeep.Constants.ACTION_END_CALL
import com.example.skype_clone.callkeep.Constants.ACTION_HOLD_CALL
import com.example.skype_clone.callkeep.Constants.ACTION_UNHOLD_CALL
import com.example.skype_clone.callkeep.Constants.EXTRA_CALLER_NAME
import com.example.skype_clone.callkeep.Constants.EXTRA_CALL_NUMBER
import com.example.skype_clone.callkeep.Constants.EXTRA_CALL_UUID

private const val TAG = "CallConnection"

@RequiresApi(Build.VERSION_CODES.M)
class CallConnection(ctx: Context, handle: HashMap<String, String>) : Connection() {

  val NOTIFICATION_CHANNEL_ID = "10001"
  val NOTIFICATION_ID = 10001
  private var isMuted = false
  private var handle: HashMap<String, String>? = null
  private val ctx: Context

  init {
    this.handle = handle
    this.ctx = ctx

    val number = handle[EXTRA_CALL_NUMBER]
    val name = handle[EXTRA_CALLER_NAME]

    if (number != null) {
      setAddress(Uri.parse(number), TelecomManager.PRESENTATION_ALLOWED)
    }
    if (name != null && name != "") {
      setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED)
    }

  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onShowIncomingCallUi() {
    Log.i(TAG, "onShowIncomingCallUi")
    /*
    flutter アプリ起動
    var i = Intent(Intent.ACTION_VIEW, Uri.parse("com.example://skype_clone"))
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    ctx.startActivity(i)
     */

    val intent = Intent(Intent.ACTION_MAIN, null)
    val dismissButtonIntent = Intent(ctx, DismissButtonReceiver::class.java)
    dismissButtonIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID)

    intent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
    intent.setClass(ctx, IncomingCallActivity::class.java!!)
    intent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID)

    val pendingIntent = PendingIntent.getActivity(ctx, 1, intent, 0)
    val pendingIntent2 = PendingIntent.getBroadcast(ctx, 0, dismissButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL_ID)
    builder.setOngoing(true)
    builder.setPriority(NotificationCompat.PRIORITY_HIGH)

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
    builder.setAutoCancel(true)
    //builder.setNotificationSilent() 通知表示せず IncomingCallActivity起動

    // Use builder.addAction(..) to add buttons to answer or reject the call.
    val acceptAction = NotificationCompat.Action.Builder(R.drawable.ic_action_call, "Accept", pendingIntent)
            .build()

    val declineAction = NotificationCompat.Action.Builder(R.drawable.ic_action_end_call, "Decline", pendingIntent2)
            .build()
    builder.addAction(acceptAction)
    builder.addAction(declineAction)

    val notificationManager = ctx.getSystemService(NotificationManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
      builder.setChannelId(NOTIFICATION_CHANNEL_ID)
      notificationManager.createNotificationChannel(notificationChannel)
    }
    notificationManager.notify("Call Notification", NOTIFICATION_ID, builder.build())
  }

  override fun onCallAudioStateChanged(state: CallAudioState?) {
    Log.i(TAG, "onCallAudioStateChanged")
  }

  override fun onAnswer() {
    super.onAnswer()
    Log.i(TAG, "onAnswer")
    connectionCapabilities = connectionCapabilities or CAPABILITY_HOLD
    audioModeIsVoip = true

    sendCallRequestToActivity(ACTION_ANSWER_CALL, handle)
    sendCallRequestToActivity(ACTION_AUDIO_SESSION, handle)
    Log.d(TAG, "onAnswer executed")
  }

  override fun onDisconnect() {
    super.onDisconnect()
    Log.i(TAG, "onDisconnect")
    setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
    sendCallRequestToActivity(ACTION_END_CALL, handle)
    try {
      CallConnectionService.deinitConnection(handle?.get(EXTRA_CALL_UUID))
    } catch (exception: Throwable) {
      Log.e(TAG, "Handle map error", exception)
    }
    destroy()
  }

  override fun onHold() {
    Log.i(TAG, "onHold")
    super.onHold()
    this.setOnHold();
    sendCallRequestToActivity(ACTION_HOLD_CALL, handle);
  }

  override fun onUnhold() {
    super.onUnhold()
    Log.i(TAG, "onUnhold")
    sendCallRequestToActivity(ACTION_UNHOLD_CALL, handle);
    setActive();
  }

  override fun onReject() {
    super.onReject()
    Log.i(TAG, "onReject")
    setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
    sendCallRequestToActivity(ACTION_END_CALL, handle)
    Log.d(TAG, "onReject executed")
    try {
      CallConnectionService.deinitConnection(handle?.get(EXTRA_CALL_UUID))
    } catch (exception: Throwable) {
      Log.e(TAG, "Handle map error", exception)
    }
    destroy()
  }

  private fun sendCallRequestToActivity(action: String, @Nullable attributeMap: HashMap<String, String>?) {
    val instance: CallConnection = this
    val handler = Handler()
    handler.post(Runnable {
      val intent = Intent(action)
      if (attributeMap != null) {
        val extras = Bundle()
        extras.putSerializable("attributeMap", attributeMap)
        intent.putExtras(extras)
      }
      LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent)
    })
  }
}
