package io.sourcya.playx_3d_scene.core.loader

import android.annotation.SuppressLint
import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.readAsset
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer


internal class GlbLoader constructor(
    private val modelViewer: CustomModelViewer?,
    private val context: Context,
    private val flutterAssets: FlutterAssets
) {
    val isLoading = MutableStateFlow(false)

    suspend fun loadGlbFromAsset(path: String?): Resource<String> {
        isLoading.value = true
        return withContext(Dispatchers.IO) {
            if (modelViewer == null) {
                isLoading.value = false
                return@withContext Resource.Error(
                    "model viewer is not initialized"
                )
            } else {
                when (val bufferResource = readAsset(path, flutterAssets, context)) {
                    is Resource.Success -> {
                        bufferResource.data?.let {
                            modelViewer.modelLoader.loadModelGlb(it, true)
                        }
                        isLoading.value = false
                        return@withContext Resource.Success("Loaded glb model successfully from ${path ?: ""}")
                    }
                    is Resource.Error -> {
                        isLoading.value = false
                        return@withContext Resource.Error(
                            bufferResource.message ?: "Couldn't load glb model from asset"
                        )
                    }
                }
            }
        }
    }

    suspend fun loadGlbFromUrl(url: String?): Resource<String> {
        isLoading.value = true
        if (modelViewer == null) {
            isLoading.value = false
            return Resource.Error(
                "model viewer is not initialized"
            )
        } else {
            return if (url.isNullOrEmpty()) {
                isLoading.value = false
                Resource.Error("Url is empty")
            } else {
                withContext(Dispatchers.IO) {
                    try {
                        URL(url).openStream().use { inputStream: InputStream ->
                            val stream = BufferedInputStream(inputStream)
                            ByteArrayOutputStream().use { output ->
                                stream.copyTo(output)
                                val byteArr = output.toByteArray()
                                val byteBuffer = ByteBuffer.wrap(byteArr)
                                val rewound = byteBuffer.rewind()
                                modelViewer.modelLoader.loadModelGlb(rewound, true)
                            }
                        }
                        isLoading.value = false
                        return@withContext Resource.Success("Loaded glb model successfully from ${url ?: ""}")
                    } catch (e: Throwable) {
                        isLoading.value = false
                        return@withContext Resource.Error("Couldn't load glb model from url: $url")
                    }

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

