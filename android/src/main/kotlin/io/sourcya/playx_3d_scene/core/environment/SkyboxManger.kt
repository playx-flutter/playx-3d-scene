package io.sourcya.playx_3d_scene.core.environment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KTX1Loader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_3d_scene.core.models.states.SceneState
import io.sourcya.playx_3d_scene.core.network.NetworkClient
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.readAsset
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SkyboxManger constructor(
    private val modelViewer: CustomModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets

) {


    fun setDefaultSkybox() {
        modelViewer.setSkyboxState(SceneState.LOADING)
        setTransparentSkybox()
        modelViewer.setSkyboxState(SceneState.LOADED)

    }

    suspend fun setSkyboxFromAsset(path: String?): Resource<String> {
        modelViewer.setSkyboxState(SceneState.LOADING)

        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        val skybox =
                            KTX1Loader.createSkybox(modelViewer.engine, it)
                        withContext(Dispatchers.Main) {
                            modelViewer.scene.skybox = skybox
                        }
                    }
                    modelViewer.setSkyboxState(SceneState.LOADED)
                    return@withContext Resource.Success("Loaded environment successfully from ${path ?: ""}")
                }
                is Resource.Error -> {
                    modelViewer.setSkyboxState(SceneState.ERROR)
                    return@withContext Resource.Error(
                        bufferResource.message ?: "Couldn't change environment from asset"
                    )
                }
            }
        }
    }

    suspend fun setSkyboxFromUrl(url: String?): Resource<String> {
        modelViewer.setSkyboxState(SceneState.LOADING)

        if (url.isNullOrEmpty()) return Resource.Error("URL is empty")

        return withContext(Dispatchers.IO) {
            val buffer = NetworkClient.downloadFile(url)
            if (buffer != null) {
                val skybox =
                    KTX1Loader.createSkybox(modelViewer.engine, buffer)
                withContext(Dispatchers.Main) {
                    modelViewer.scene.skybox = skybox
                }
                modelViewer.setSkyboxState(SceneState.LOADED)
                return@withContext Resource.Success("Loaded skybox successfully from ${url ?: ""}")

            } else {
                modelViewer.setSkyboxState(SceneState.ERROR)
                return@withContext Resource.Error(
                    "Couldn't load skybox form $url"
                )
            }
        }
    }


    fun setSkyboxFromColor(color: Int?): Resource<String> {
        modelViewer.setSkyboxState(SceneState.LOADING)

        if (color == null) {
            modelViewer.setSkyboxState(SceneState.ERROR)
            return Resource.Error("Color is Invalid")
        }
        val red: Float = Color.red(color) / 255f
        val green: Float = Color.green(color) / 255f
        val blue: Float = Color.blue(color) / 255f
        val alpha: Float = Color.alpha(color) / 255f

        val skybox =
            Skybox.Builder().color(red, green, blue, alpha)
                .build(modelViewer.engine)
        modelViewer.scene.skybox = skybox
        modelViewer.setSkyboxState(SceneState.LOADED)
        return Resource.Success("Loaded environment successfully from color")
    }

    fun setTransparentSkybox() {
        modelViewer.scene.skybox = null
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: SkyboxManger? = null

        fun getInstance(
            modelViewer: CustomModelViewer,
            context: Context,
            flutterAssets: FlutterPlugin.FlutterAssets
        ): SkyboxManger =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SkyboxManger(modelViewer, context, flutterAssets).also {
                    INSTANCE = it
                }
            }

    }

}