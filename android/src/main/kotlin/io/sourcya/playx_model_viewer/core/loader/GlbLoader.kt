package io.sourcya.playx_model_viewer.core.loader

import android.annotation.SuppressLint
import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_model_viewer.core.utils.Resource
import io.sourcya.playx_model_viewer.core.utils.readAsset
import io.sourcya.playx_model_viewer.core.viewer.CustomModelViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
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
    suspend fun loadGlbFromAsset(path: String?): Resource<String> {
        return withContext(Dispatchers.IO) {
            if (modelViewer == null) {
                return@withContext Resource.Error(
                    "model viewer is not initialized"
                )
            } else {
                when (val bufferResource = readAsset(path, flutterAssets, context)) {
                    is Resource.Success -> {
                        bufferResource.data?.let {
                            modelViewer.modelLoader.loadModelGlb(it, true)
                        }
                        return@withContext Resource.Success("Loaded glb model successfully from ${path ?: ""}")
                    }
                    is Resource.Error -> {
                        return@withContext Resource.Error(
                            bufferResource.message ?: "Couldn't load glb model from asset"
                        )
                    }
                }
            }
        }
    }

    suspend fun loadGlbFromUrl(url: String?): Resource<String> {
        if (modelViewer == null) {
            return Resource.Error(
                "model viewer is not initialized"
            )
        } else {
            return if (url.isNullOrEmpty()) {
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
                        return@withContext Resource.Success("Loaded glb model successfully from ${url ?: ""}")
                    } catch (e: Throwable) {
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

