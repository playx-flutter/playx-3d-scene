package io.sourcya.playx_3d_scene.factory

import android.content.Context
import com.google.android.filament.Engine
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.ResourceLoader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.sourcya.playx_3d_scene.core.utils.IBLProfiler
import io.sourcya.playx_3d_scene.utils.LifecycleProvider
import io.sourcya.playx_3d_scene.view.Playx3dScene

class Playx3dSceneFactory(private val binding: FlutterPlugin.FlutterPluginBinding,
                          private val engine:Engine,
                          private val iblProfiler: IBLProfiler,
                          private val assetLoader: AssetLoader,
                          private val resourceLoader: ResourceLoader,
                          private val lifecycleProvider: LifecycleProvider
):PlatformViewFactory (StandardMessageCodec.INSTANCE){

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = try {
            args as Map<String?, Any?>?
        }catch (_:Throwable){
            null
        }

        return Playx3dScene(
            context,
            viewId,
            creationParams ,
            binding,
            engine,
            iblProfiler,
            assetLoader,
            resourceLoader,
            lifecycleProvider
        )
    }
}