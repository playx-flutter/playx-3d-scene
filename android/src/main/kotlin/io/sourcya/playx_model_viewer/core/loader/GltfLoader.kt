package io.sourcya.playx_model_viewer.core.loader

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.filament.utils.ModelViewer
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_model_viewer.core.utils.readAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer


class GltfLoader private constructor(
    private val modelViewer: ModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterAssets

) {


    suspend fun loadGltfFromAsset(
        path: String,
        gltfImagePathPrefix: String,
        gltfImagePathPostfix: String
    ) {
        withContext(Dispatchers.IO) {
            val buffer = readAsset(path, flutterAssets, context)
            withContext(Dispatchers.Main) {

                buffer?.let {
                    modelViewer.destroyModel()
                    modelViewer.loadModelGltf(it) { uri ->

                        val assetPath =gltfImagePathPrefix+ uri+gltfImagePathPostfix
                        readAsset(assetPath, flutterAssets, context)
                    }

                    modelViewer.transformToUnitCube()
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
        private var INSTANCE: GltfLoader? = null

        fun getInstance(modelViewer: ModelViewer, context: Context,  flutterAssets :FlutterAssets): GltfLoader =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: GltfLoader(modelViewer, context,flutterAssets).also {
                    INSTANCE = it
                }
            }

    }
}

