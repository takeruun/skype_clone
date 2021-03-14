package com.example.skype_clone

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.PluginRegistry

class CallKeepPlugin : EventChannel.StreamHandler, FlutterPlugin {
    private lateinit var eventChannel: EventChannel

    companion object {
        private var mEventSink: EventChannel.EventSink? = null
        private const val EVENT_CHANNEL = "com.example.skype_clone/call_event"
        private var status: Boolean = false
        @JvmStatic
        fun registerWith(registrar: PluginRegistry.Registrar) {
            CallKeepPlugin().apply {
                initPlugin(registrar.context(), registrar.messenger())
            }
            status = true
        }

        fun getStatu(): Boolean {
            return status
        }
    }

    private fun initPlugin(context: Context, binaryMessenger: BinaryMessenger){
        eventChannel = EventChannel(binaryMessenger, EVENT_CHANNEL)
        eventChannel.setStreamHandler(this)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        mEventSink = events
    }

    override fun onCancel(arguments: Any?) {}

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        initPlugin(binding.applicationContext, binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        eventChannel.setStreamHandler(null)
    }
}