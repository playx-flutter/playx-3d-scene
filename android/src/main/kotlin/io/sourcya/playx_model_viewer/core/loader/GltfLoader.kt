package io.sourcya.playx_model_viewer.core.loader

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.filament.utils.ModelViewer
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_model_viewer.core.utils.Resource
import io.sourcya.playx_model_viewer.core.utils.readAsset
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer


internal class GltfLoader private constructor(
    private val modelViewer: ModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterAssets

) {
    suspend fun loadGltfFromAsset(
        path: String?,
        gltfImagePathPrefix: String,
        gltfImagePathPostfix: String
    ) :Resource<String>{

        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                            try {
                                withContext(Dispatchers.Main){
                                    modelViewer.destroyModel()
                                    modelViewer.loadModelGltf(it){ uri ->
                                        val assetPath = gltfImagePathPrefix + uri + gltfImagePathPostfix
                                        val assetResource =  readAsset(assetPath, flutterAssets, context)
                                        assetResource.data
                                    }
                                    modelViewer.transformToUnitCube()

                                }
                            }catch (t:Throwable){
                                return@withContext Resource.Error("Failed to load gltf")
                            }
                    }
                    return@withContext Resource.Success("Loaded glb model successfully from ${path ?: ""}")
                }
                is Resource.Error -> {
                    return@withContext Resource.Error(
                        bufferResource.message ?: "Couldn't load gltf model from asset"
                    )
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
