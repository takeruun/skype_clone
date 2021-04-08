package com.example.skype_clone

import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.shim.ShimPluginRegistry

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val shimPluginRegistry = ShimPluginRegistry(flutterEngine)
        CallKeepPlugin.registerWith(shimPluginRegistry.registrarFor("com.example.skype_clone.CallKeepPlugin"))
    }

}
