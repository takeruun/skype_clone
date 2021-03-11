package com.example.skype_clone

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telecom.*
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.wazo.callkeep.Constants.*
import java.util.*
import kotlin.collections.HashMap

private const val TAG = "CallConnectionService"

@RequiresApi(Build.VERSION_CODES.M)
class CallConnectionService : ConnectionService() {
  private var isAvailable: Boolean
  private var isInitialized: Boolean
  private var isReachable: Boolean
  private var notReachableCallUuid: String? = null
  private var currentConnectionRequest: ConnectionRequest?
  private var phoneAccountHandle: PhoneAccountHandle? = null
  val currentConnectionService: CallConnectionService

  init {
    Log.d(TAG, "Constructor")
    isReachable = false
    isInitialized = false
    isAvailable = false
    currentConnectionRequest = null
    currentConnectionService = this
  }

  fun setPhoneAccountHandle(phoneAccountHandle: PhoneAccountHandle?) {
    this.phoneAccountHandle = phoneAccountHandle
  }

  fun setAvailable(value: Boolean) {
    Log.d(TAG, "setAvailable: " + if (value) "true" else "false")
    if (value) {
      isInitialized = true
    }
    isAvailable = value
  }

  fun setReachable() {
    Log.d(TAG, "setReachable")
    isReachable = true
    this.currentConnectionRequest = null
  }

  companion object {
    var currentConnections: HashMap<String, CallConnection> = HashMap()
    var hasOutgoingCall = false

    fun getConnection(connectionId: String?): Connection? {
      return if (currentConnections.containsKey(connectionId)) {
        currentConnections[connectionId]
      } else null
    }

    fun deinitConnection(connectionId: String?) {
      hasOutgoingCall = false
      if (currentConnections.containsKey(connectionId)) {
        currentConnections.remove(connectionId)
      }
    }
  }

  override fun onCreateOutgoingConnection(
      connectionManagerPhoneAccount: PhoneAccountHandle?,
      request: ConnectionRequest
  ): Connection {
    Log.i(TAG, "onCreateOutgoingConnection")
    hasOutgoingCall = true
    val uuid: String = UUID.randomUUID().toString()
    if (!isInitialized && !isReachable) {
      this.notReachableCallUuid = uuid
      this.currentConnectionRequest = request
      this.checkReachability()
    }

    return this.makeOutgoingCall(request, uuid, false)
  }

  override fun onCreateOutgoingConnectionFailed(
      connectionManagerPhoneAccount: PhoneAccountHandle?,
      request: ConnectionRequest?
  ) {
    super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
    Log.i(TAG, "create outgoing call failed")
  }

  @RequiresApi(Build.VERSION_CODES.Q)
  override fun onCreateIncomingConnection(
      connectionManagerPhoneAccount: PhoneAccountHandle?,
      request: ConnectionRequest
  ): Connection {
    Log.i(TAG, "onCreateIncomingConnection")
    val extra = request.extras
    val number: Uri = request.address
    val name = extra.getString(EXTRA_CALLER_NAME)
    val incomingCallConnection = createConnection(request)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
      incomingCallConnection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
    }
    incomingCallConnection.setRinging()
    //incomingCallConnection.onSilence()
    incomingCallConnection.setInitialized()

    //val soundIntent = Intent(this, RingSoundService::class.java)
    //startService(soundIntent)

    return incomingCallConnection
  }

  override fun onCreateIncomingConnectionFailed(
      connectionManagerPhoneAccount: PhoneAccountHandle?,
      request: ConnectionRequest?
  ) {
    super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
    Log.i(TAG, "create outgoing call failed ")
  }

  private fun makeOutgoingCall(
      request: ConnectionRequest,
      uuid: String,
      forceWakeUp: Boolean
  ): Connection {
    val extras = request.extras
    val outgoingCallConnection: Connection
    val number = request.address.schemeSpecificPart
    val extrasNumber = extras.getString(EXTRA_CALL_NUMBER)
    val displayName = extras.getString(EXTRA_CALLER_NAME)
    val isForeground = this.isRunning(this.applicationContext)

    Log.d(TAG, "makeOutgoingCall:" + uuid + ", number: " + number + ", displayName:" + displayName)

    // Wakeup application if needed
    if (!isForeground || forceWakeUp) {
      Log.d(TAG, "onCreateOutgoingConnection: Waking up application")
      this.wakeUpApplication(uuid, number, displayName!!)
    } else if (!this.canMakeOutgoingCall() && isReachable) {
      Log.d(TAG, "onCreateOutgoingConnection: not available")
      return Connection.createFailedConnection(DisconnectCause(DisconnectCause.LOCAL))
    }

    // TODO: Hold all other calls
    if (extrasNumber == null || !extrasNumber.equals(number)) {
      extras.putString(EXTRA_CALL_UUID, uuid)
      extras.putString(EXTRA_CALLER_NAME, displayName)
      extras.putString(EXTRA_CALL_NUMBER, number)
    }

    outgoingCallConnection = createConnection(request)
    outgoingCallConnection.setDialing()
    outgoingCallConnection.setAudioModeIsVoip(true)
    outgoingCallConnection.setCallerDisplayName(displayName, TelecomManager.PRESENTATION_ALLOWED)

    // ‍️Weirdly on some Samsung phones (A50, S9...) using `setInitialized` will not display the
    // native UI ...
    // when making a call from the native Phone application. The call will still be displayed
    // correctly without it.
    if (!Build.MANUFACTURER.equals("Samsung", ignoreCase = true)) {
      outgoingCallConnection.setInitialized()
    }

    val extrasMap = bundleToMap(extras)

    sendCallRequestToActivity(ACTION_ONGOING_CALL, extrasMap!!)
    sendCallRequestToActivity(ACTION_AUDIO_SESSION, extrasMap!!)

    Log.d(TAG, "onCreateOutgoingConnection: calling")

    return outgoingCallConnection
  }

  private fun wakeUpApplication(uuid: String, number: String, displayName: String) {
    val extrasMap: HashMap<String, String> = HashMap()
    extrasMap[EXTRA_CALL_UUID] = uuid
    extrasMap[EXTRA_CALLER_NAME] = displayName
    extrasMap[EXTRA_CALL_NUMBER] = number
    sendCallRequestToActivity(ACTION_WAKE_APP, extrasMap)
  }

  private fun createConnection(request: ConnectionRequest): Connection {
    val extras: Bundle = request.extras
    val extrasMap: HashMap<String, String>? = this.bundleToMap(extras)
    extrasMap!![EXTRA_CALL_NUMBER] = request.address.toString()
    val connection = CallConnection(applicationContext, extrasMap)

    connection.connectionCapabilities =
        Connection.CAPABILITY_MUTE or Connection.CAPABILITY_SUPPORT_HOLD
    connection.setAddress(request!!.address, TelecomManager.PRESENTATION_ALLOWED)
    connection.setInitializing()
    connection.setExtras(extras)
    currentConnections.put(extras.getString(EXTRA_CALL_UUID)!!, connection)

    // Get other connections for conferencing
    val otherConnections: MutableMap<String, CallConnection> = HashMap()
    for ((key, value) in currentConnections) {
      if (extras.getString(EXTRA_CALL_UUID) != key) {
        otherConnections[key] = value
      }
    }
    val conferenceConnections: List<Connection> = ArrayList<Connection>(otherConnections.values)
    connection.setConferenceableConnections(conferenceConnections)

    return connection
  }

  private fun bundleToMap(extras: Bundle): HashMap<String, String>? {
    val extrasMap: HashMap<String, String> = HashMap()
    val keySet = extras.keySet()
    val iterator: Iterator<String> = keySet.iterator()
    while (iterator.hasNext()) {
      val key = iterator.next()
      if (extras[key] != null) {
        extrasMap[key] = extras[key].toString()
      }
    }
    return extrasMap
  }

  private fun wakeUpAfterReachabilityTimeout(request: ConnectionRequest) {
    if (currentConnectionRequest == null) {
      return
    }
    Log.d(TAG, "checkReachability timeout, force wakeup")
    val extras = request.extras
    val number = request.address.schemeSpecificPart
    val displayName = extras.getString(EXTRA_CALLER_NAME)
    wakeUpApplication(notReachableCallUuid!!, number, displayName!!)
    this.currentConnectionRequest = null
  }

  private fun checkReachability() {
    Log.d(TAG, "checkReachability")
    val instance: CallConnectionService = this
    sendCallRequestToActivity(ACTION_CHECK_REACHABILITY, null)
    Handler()
        .postDelayed(
            { instance.wakeUpAfterReachabilityTimeout(this.currentConnectionRequest!!) }, 2000)
  }

  private fun canMakeOutgoingCall(): Boolean {
    return isAvailable
  }

  private fun sendCallRequestToActivity(
      action: String,
      @Nullable attributeMap: HashMap<String, String>?
  ) {
    val instance: CallConnectionService = this
    val handler = Handler()
    handler.post(
        Runnable {
          val intent = Intent(action)
          if (attributeMap != null) {
            val extras = Bundle()
            extras.putSerializable("attributeMap", attributeMap)
            intent.putExtras(extras)
          }
          LocalBroadcastManager.getInstance(instance).sendBroadcast(intent)
        })
  }

  /**
   * https://stackoverflow.com/questions/5446565/android-how-do-i-check-if-activity-is-running
   *
   * @param context Context
   * @return boolean
   */
  fun isRunning(context: Context): Boolean {
    val activityManager: ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val tasks: List<ActivityManager.RunningTaskInfo> =
        activityManager.getRunningTasks(Int.MAX_VALUE)
    for (task in tasks) {
      if (context.getPackageName().equals(task.baseActivity?.getPackageName(), ignoreCase = true))
          return true
    }
    return false
  }
}
