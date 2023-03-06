package io.sourcya.playx_3d_scene.core.loader

import android.annotation.SuppressLint
import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_3d_scene.core.models.ModelState
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.readAsset
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer


internal class GltfLoader  constructor(
    private val modelViewer: CustomModelViewer?,
    private val context: Context,
    private val flutterAssets: FlutterAssets

) {
     val state = MutableStateFlow(ModelState.NONE)
    suspend fun loadGltfFromAsset(
        path: String?,
        gltfImagePathPrefix: String,
        gltfImagePathPostfix: String,
        isFallback: Boolean = false
    ) :Resource<String>{
        state.value = ModelState.LOADING

        return withContext(Dispatchers.IO) {
            if(modelViewer == null) {
                state.value = ModelState.ERROR
                return@withContext Resource.Error(
                    "model viewer is not initialized"
                )
            }else {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        try {
                                modelViewer.modelLoader.loadModelGltfAsync(it,true) { uri ->
                                    val assetPath = gltfImagePathPrefix + uri + gltfImagePathPostfix
                                    val assetResource = readAsset(assetPath, flutterAssets, context)
                                    assetResource.data
                                }


                        } catch (t: Throwable) {
                            state.value = ModelState.ERROR
                            return@withContext Resource.Error("Failed to load gltf")
                        }
                    }
                    state.value = if(isFallback)ModelState.FALLBACK_LOADED else  ModelState.LOADED
                    return@withContext Resource.Success("Loaded glb model successfully from ${path ?: ""}")
                }
                is Resource.Error -> {
                    state.value = ModelState.ERROR
                    return@withContext Resource.Error(
                        bufferResource.message ?: "Couldn't load gltf model from asset"
                    )
                }
            }
            }

        }

    }


    suspend fun loadGltfFromUrl(url: String) {
        withContext(Dispatchers.IO) {

            URL(url).openStream().use { inputStream: InputStream ->
                val stream = BufferedInputStream(inputStream)
                ByteArrayOutputStream().use { output ->
                    stream.copyTo(output)
                    val byteArr = output.toByteArray()
                    val byteBuffer = ByteBuffer.wrap(byteArr)
                    val rewound = byteBuffer.rewind()
                    withContext(Dispatchers.Main) {
                        modelViewer?.destroyModel()
                        modelViewer?.modelLoader?.loadModelGlb(rewound)
                        modelViewer?.transformToUnitCube()
                    }
                }
            }

        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: GltfLoader? = null

        fun getInstance(modelViewer: CustomModelViewer, context: Context,  flutterAssets :FlutterAssets): GltfLoader =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: GltfLoader(modelViewer, context,flutterAssets).also {
                    INSTANCE = it
                }
            }

    }
}

