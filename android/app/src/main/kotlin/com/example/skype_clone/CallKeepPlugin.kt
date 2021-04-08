package com.example.skype_clone

import android.app.Activity
import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.*

class CallKeepPlugin : FlutterPlugin, ActivityAware {
   private var callKeepHandler: CallKeep? = null

    companion object {
        private const val EVENT_CHANNEL = "com.example.skype_clone/call_event"
        private const val METHOD_CHANNEL = "com.exmaple.skype_clone/call"
        private var status: Boolean = false
        @JvmStatic
        fun registerWith(registrar: PluginRegistry.Registrar) {
            val eventChannel = EventChannel(registrar.messenger(), EVENT_CHANNEL)
            val methodChannel = MethodChannel(registrar.messenger(), METHOD_CHANNEL)

            val plugin = CallKeep(methodChannel, eventChannel, registrar.context().applicationContext)
            plugin.currentActivity = registrar.activity()
            status = true
        }

        fun getStatus(): Boolean {
            return status
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        val eventChannel = EventChannel(binding.binaryMessenger, EVENT_CHANNEL)
        val methodChannel = MethodChannel(binding.binaryMessenger, METHOD_CHANNEL)
        val plugin = CallKeep(methodChannel, eventChannel, binding.applicationContext)

        callKeepHandler = plugin
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        callKeepHandler?.stopListening()
        callKeepHandler = null
    }

    override  fun onAttachedToActivity(binding: ActivityPluginBinding){
        val plugin = callKeepHandler ?: return

        callKeepHandler?.currentActivity = binding.activity
    }

    override fun onDetachedFromActivity() {
        callKeepHandler?.currentActivity = null
    }



    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

}