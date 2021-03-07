package com.example.skype_clone

import android.content.*
import android.os.Build
import android.telecom.*
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log

class CallConnectionService : ConnectionService() {
  override fun onCreateOutgoingConnection(
      connectionManagerPhoneAccount: PhoneAccountHandle?,
      request: ConnectionRequest?
  ): Connection {
    Log.i("CallConnectionService", "onCreateOutgoingConnection")
    val conn = CallConnection(applicationContext)
    conn.setAddress(request!!.address, TelecomManager.PRESENTATION_ALLOWED)
    conn.setInitializing()
    // conn.videoProvider = MyVideoProvider()
    conn.setActive()
    return conn
  }

  override fun onCreateOutgoingConnectionFailed(
      connectionManagerPhoneAccount: PhoneAccountHandle?,
      request: ConnectionRequest?
  ) {
    super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
    Log.i("CallConnectionService", "create outgoing call failed")
  }

  override fun onCreateIncomingConnection(
      connectionManagerPhoneAccount: PhoneAccountHandle?,
      request: ConnectionRequest?
  ): Connection {
    Log.i("CallConnectionService", "onCreateIncomingConnection")
    val conn = CallConnection(applicationContext)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
      conn.connectionProperties = Connection.PROPERTY_SELF_MANAGED
    }
    conn.setCallerDisplayName("test call", TelecomManager.PRESENTATION_ALLOWED)
    conn.setAddress(request!!.address, TelecomManager.PRESENTATION_ALLOWED)
    conn.setInitializing()
    // conn.videoProvider = MyVideoProvider()
    conn.setActive()

    return conn
  }

  override fun onCreateIncomingConnectionFailed(
      connectionManagerPhoneAccount: PhoneAccountHandle?,
      request: ConnectionRequest?
  ) {
    super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
    Log.i("CallConnectionService", "create outgoing call failed ")
  }
}
