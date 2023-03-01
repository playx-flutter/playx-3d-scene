package io.sourcya.playx_model_viewer.core.loader

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.filament.utils.ModelViewer
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_model_viewer.core.utils.Resource
import io.sourcya.playx_model_viewer.core.utils.readAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer


internal class GlbLoader private constructor(
    private val modelViewer: ModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterAssets
) {


    suspend fun loadGlbFromAsset(path: String?): Resource<String> {
        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        withContext(Dispatchers.Main) {
                            modelViewer.destroyModel()
                            modelViewer.loadModelGlb(it)
                            modelViewer.transformToUnitCube()
                        }
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

    suspend fun loadGlbFromUrl(url: String?): Resource<String> {
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
                            withContext(Dispatchers.Main) {
                                modelViewer.loadModelGlb(rewound)
                                modelViewer.transformToUnitCube()
                            }
                        }
                    }
                    return@withContext Resource.Success("Loaded glb model successfully from ${url ?: ""}")
                } catch (e: Throwable) {
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
            modelViewer: ModelViewer,
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

