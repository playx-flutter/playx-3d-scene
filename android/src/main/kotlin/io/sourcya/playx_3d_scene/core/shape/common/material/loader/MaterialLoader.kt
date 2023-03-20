package io.sourcya.playx_3d_scene.core.shape.common.material.loader

import android.content.Context
import com.google.android.filament.Material
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_3d_scene.core.network.NetworkClient
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.readAsset
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MaterialLoader (
    private val modelViewer: CustomModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets
) {
    val engine = modelViewer.engine

    suspend fun loadMaterialFromAsset(path: String?, ): Resource<Material> {
        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                 val buffer=   bufferResource.data!!
                    val material=   Material.Builder().payload(buffer,buffer.remaining()).build(engine)
                    return@withContext Resource.Success(material)
                }
                is Resource.Error -> {
                    return@withContext Resource.Error(
                        bufferResource.message ?: "Couldn't load material from asset"
                    )
                }
            }
        }
    }

    suspend fun loadMaterialFromUrl(url: String?,
    ): Resource<Material> {
        return if (url.isNullOrEmpty()) {
            Resource.Error("Url is empty")
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val buffer = NetworkClient.downloadFile(url)
                    if (buffer != null) {
                        val material=  Material.Builder().payload(buffer,buffer.remaining()).build(engine)
                        return@withContext Resource.Success(material)
                    }else {
                        return@withContext Resource.Error("Couldn't load material from url: $url")
                    }
                } catch (e: Throwable) {
                    return@withContext Resource.Error("Couldn't load material from url: $url")
                }

            }
        }
    }


}