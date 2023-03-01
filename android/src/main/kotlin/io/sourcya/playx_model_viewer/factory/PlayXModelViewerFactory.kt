package io.sourcya.playx_model_viewer.factory

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.sourcya.playx_model_viewer.view.LifecycleProvider
import io.sourcya.playx_model_viewer.view.PlayXModelViewer
import timber.log.Timber

class PlayXModelViewerFactory(private val binding: FlutterPlugin.FlutterPluginBinding, private val lifecycleProvider: LifecycleProvider):PlatformViewFactory (StandardMessageCodec.INSTANCE){


    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = try {
            args as Map<String?, Any?>?
        }catch (_:Throwable){
            null
        }
        return PlayXModelViewer(
            context,
            viewId,
            creationParams ,
            binding,
            lifecycleProvider
        )
    }
}