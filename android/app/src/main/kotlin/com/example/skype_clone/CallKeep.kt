package com.example.skype_clone

import android.app.Activity
import android.content.Context
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

private val TAG = "CallKeep"

class CallKeep(private val methodChannel: MethodChannel, private val eventChannel: EventChannel, private var applicationContext: Context) : MethodChannel.MethodCallHandler, EventChannel.StreamHandler {
    internal var currentActivity: Activity? = null

    init {
        methodChannel.setMethodCallHandler(this)
        eventChannel.setStreamHandler(this)
    }

    internal fun stopListening(){
        methodChannel.setMethodCallHandler(null)
        eventChannel.setStreamHandler(null)
    }

    companion object{
        private var mEventSink: EventChannel.EventSink? = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result){}

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        mEventSink = events
    }

    override fun onCancel(arguments: Any?) {}
}