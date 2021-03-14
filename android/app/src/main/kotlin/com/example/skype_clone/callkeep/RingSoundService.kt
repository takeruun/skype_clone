package com.example.skype_clone.callkeep

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.util.Log

private const val TAG = "RingSoundService"

class RingSoundService : Service() {
    private var sound: Ringtone? = null

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        val manager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        sound = RingtoneManager.getRingtone(getApplicationContext(), manager)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        sound!!.play()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        sound!!.stop()
    }
}