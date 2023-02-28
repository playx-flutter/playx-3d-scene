package io.sourcya.playx_model_viewer

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.sourcya.playx_model_viewer.factory.PlayXModelViewerFactory
import timber.log.Timber

/** PlayxModelViewerPlugin */
class PlayxModelViewerPlugin: FlutterPlugin {

  override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    Timber.plant(Timber.DebugTree())
    binding
      .platformViewRegistry
      .registerViewFactory(viewType, PlayXModelViewerFactory(binding))

  }


  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {

  }


  companion object{
     const val channelName = "io.sourcya.playx.model_viewer.channel";
    var viewType = "${channelName}_model_view"
  }

}
