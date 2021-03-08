package com.example.skype_clone

import android.content.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.*
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.skype_clone.Constants.SKYPE_PREF
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.wazo.callkeep.Constants


private const val TAG = "MyFirebaseMsgService"

@RequiresApi(Build.VERSION_CODES.M)
class MyFirebaseMessagingService : FirebaseMessagingService() {
  private var telecomManager: TelecomManager? = null
  private var phoneAccountHandle: PhoneAccountHandle? = null
  private var telephony: TelephonyManager? = null
  private var sharedPreferences: SharedPreferences? = null
  /**
   * Called when message is received.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  // [START receive_message]
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    try {
      registerPhoneAccount(this)
      // VoiceConnectionService.setPhoneAccountHandle(phoneAccountHandle)

      val extras = Bundle()
      val uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, "09022334455", null)
      extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri)
      extras.putString(Constants.EXTRA_CALLER_NAME, "THEOESP")
      extras.putString(Constants.EXTRA_CALL_UUID, "019100-192819")

      sharedPreferences = applicationContext.getSharedPreferences(SKYPE_PREF, Context.MODE_PRIVATE)
      val editor = sharedPreferences!!.edit()
      editor.putString(Constants.EXTRA_CALL_NUMBER, "09022334455")
      editor.putString(Constants.EXTRA_CALLER_NAME, "THEOESP")
      editor.putString(Constants.EXTRA_CALL_UUID, "019100-192819")
      editor.apply()

      telecomManager!!.addNewIncomingCall(phoneAccountHandle, extras)
    } catch (e: Exception) {
      Log.e("error", e.toString())
    }
    Log.d(TAG, "From: ${remoteMessage.from}")
    if (remoteMessage.data.isNotEmpty()) {
      Log.d(TAG, "Message data payload: ${remoteMessage.data}")
    }
    remoteMessage.notification?.let { Log.d(TAG, "Message Notification Body: ${it.body}") }
  }
  // [END receive_message]
  private fun registerPhoneAccount(context: Context) {
    telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    val componentName = ComponentName(context, CallConnectionService::class.java)
    phoneAccountHandle = PhoneAccountHandle(componentName, "Admin")
    val phoneAccount =
        PhoneAccount.builder(phoneAccountHandle, "Admin")
            .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
            .build()

    telecomManager!!.registerPhoneAccount(phoneAccount)
    val intent = Intent()
    intent.component =
        ComponentName(
            "com.android.server.telecom",
            "com.android.server.telecom.settings.EnableAccountPreferenceActivity")
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
  }
}
