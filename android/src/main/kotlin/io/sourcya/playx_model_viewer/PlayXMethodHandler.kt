package io.sourcya.playx_model_viewer

import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class PlayXMethodHandler (private val assets: FlutterAssets) :MethodCallHandler {

    companion object{

    }


    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        Log.d("PLAYX_MODEL_VIEWER", call.arguments.toString())

        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        }else {
            result.notImplemented()
        }

    }






}