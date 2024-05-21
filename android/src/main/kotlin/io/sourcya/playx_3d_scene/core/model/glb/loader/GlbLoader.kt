package io.sourcya.playx_3d_scene.core.model.glb.loader

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_3d_scene.core.model.common.model.ModelState
import io.sourcya.playx_3d_scene.core.network.NetworkClient
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.readAsset
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


internal class GlbLoader(
    private val modelViewer: CustomModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterAssets,
) {

    suspend fun loadGlbFromAsset(path: String?,
                                 scale: Float?,
                                 centerPosition: Position?,
                                 isFallback: Boolean = false,

                                 ): Resource<String> {
        modelViewer.setModelState( ModelState.LOADING)

        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        modelViewer.modelLoader.loadModelGlb(it, true,centerPosition,scale)
                    }
                    modelViewer.setModelState( if(isFallback) ModelState.FALLBACK_LOADED else  ModelState.LOADED)
                    return@withContext Resource.Success("Loaded glb model successfully from ${path ?: ""}")
                }
                is Resource.Error -> {
                    modelViewer.setModelState( ModelState.ERROR)
                    return@withContext Resource.Error(
                        bufferResource.message ?: "Couldn't load glb model from asset"
                    )
                }
            }
        }
    }

    suspend fun loadGlbFromUrl(url: String?,
                               scale: Float?,
                               centerPosition: Position?,
                               isFallback: Boolean = false,
                               ): Resource<String> {
        modelViewer.setModelState( ModelState.LOADING)
        return if (url.isNullOrEmpty()) {
            modelViewer.setModelState( ModelState.ERROR)
            Resource.Error("Url is empty")
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val buffer = NetworkClient.downloadFile(url)
                    if (buffer != null) {
                        modelViewer.modelLoader.loadModelGlb(buffer, true,centerPosition,scale)
                        modelViewer.setModelState(
                        if(isFallback) ModelState.FALLBACK_LOADED else  ModelState.LOADED)
                        return@withContext Resource.Success("Loaded glb model successfully from $url")
                    }else {
                        modelViewer.setModelState( ModelState.ERROR)
                        return@withContext Resource.Error("Couldn't load glb model from url: $url")
                    }
                } catch (e: Throwable) {
                    modelViewer.setModelState( ModelState.ERROR)
                    return@withContext Resource.Error("Couldn't load glb model from url: $url")
                }

            }
        }
    }

}

