package io.sourcya.playx_model_viewer.factory

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.sourcya.playx_model_viewer.view.PlayXModelViewer
import timber.log.Timber

class PlayXModelViewerFactory(private val binding: FlutterPlugin.FlutterPluginBinding):PlatformViewFactory (StandardMessageCodec.INSTANCE){


    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?

        Timber.d("PlayXModelViewerFactory: creationParams = $creationParams")
        return PlayXModelViewer(
            context,
            viewId,
            creationParams ,
            binding
        )
    }
}