package io.sourcya.playx_model_viewer.core.loader

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.filament.utils.ModelViewer
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_model_viewer.core.utils.readAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer


class GlbLoader private constructor(
    private val modelViewer: ModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterAssets
) {


    suspend fun loadGlbFromAsset(path: String) {
        withContext(Dispatchers.IO) {
            val buffer = readAsset(path, flutterAssets, context)
            withContext(Dispatchers.Main) {
                Timber.d("buffer is null: ${buffer == null}")
                buffer?.let {
                    modelViewer.destroyModel()
                    modelViewer.loadModelGlb(it)
                    modelViewer.transformToUnitCube()
                }
            }

        }

    }

    suspend fun loadGlbFromUrl(url: String) {
        withContext(Dispatchers.IO) {
            URL(url).openStream().use { inputStream: InputStream ->
                val stream = BufferedInputStream(inputStream)
                ByteArrayOutputStream().use { output ->
                    stream.copyTo(output)
                    val byteArr = output.toByteArray()
                    val byteBuffer = ByteBuffer.wrap(byteArr)
                    val rewound = byteBuffer.rewind()
                    withContext(Dispatchers.Main) {
                        modelViewer.destroyModel()
                        modelViewer.loadModelGlb(rewound)
                        modelViewer.transformToUnitCube()
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

