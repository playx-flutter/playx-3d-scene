package io.sourcya.playx_3d_scene.core.loader

import android.annotation.SuppressLint
import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_3d_scene.core.models.states.ModelState
import io.sourcya.playx_3d_scene.core.network.NetworkClient
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.readAsset
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


internal class GlbLoader constructor(
    private val modelViewer: CustomModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterAssets
) {

    suspend fun loadGlbFromAsset(path: String?,
                                 isFallback: Boolean = false
    ): Resource<String> {
        modelViewer.setModelState( ModelState.LOADING)
        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        modelViewer.modelLoader.loadModelGlb(it, true)
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
                               isFallback: Boolean = false
    ): Resource<String> {
        modelViewer.setModelState( ModelState.LOADING)
        return if (url.isNullOrEmpty()) {
            modelViewer.setModelState( ModelState.ERROR)
            Resource.Error("Url is empty")
        } else {
            withContext(Dispatchers.IO) {
                Timber.d("downloadFile : Got buffer: loadGlbFromUrl")

                try {

                    val buffer = NetworkClient.downloadFile(url)
                    Timber.d("downloadFile : Got buffer: ${buffer == null}")
                    if (buffer != null) {
                        modelViewer.modelLoader.loadModelGlb(buffer, true)
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

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: GlbLoader? = null

        fun getInstance(
            modelViewer: CustomModelViewer,
            context: Context,
            flutterAssets: FlutterAssets
        ): GlbLoader =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: GlbLoader(modelViewer, context, flutterAssets).also {
                    INSTANCE = it
                }
            }

    }
}

