package io.sourcya.playx_model_viewer.core.environment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KTX1Loader
import com.google.android.filament.utils.ModelViewer
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_model_viewer.core.utils.Resource
import io.sourcya.playx_model_viewer.core.utils.readAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class EnvironmentManger private constructor(
    private val modelViewer: ModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets

) {


    fun setDefaultEnvironment() {
        setTransparentEnvironment()
    }

    suspend fun setEnvironmentFromAsset(path: String?): Resource<String> {
     return   withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        val skybox =
                            KTX1Loader.createSkybox(modelViewer.engine, it)
                        withContext(Dispatchers.Main) {
                            modelViewer.scene.skybox = skybox
                        }
                    }
                  return@withContext  Resource.Success("Loaded environment successfully from ${path?:""}")
                }
                is Resource.Error -> {
                    return@withContext Resource.Error(bufferResource.message ?:"Couldn't change environment from asset")
                }
            }
        }
    }


     fun setEnvironmentFromColor(color: Int?) : Resource<String> {
        if (color == null) return Resource.Error("Color is Invalid" )
        val red: Float = Color.red(color) / 255f
        val green: Float = Color.green(color) / 255f
        val blue: Float = Color.blue(color) / 255f
        val alpha: Float = Color.alpha(color) / 255f

        val skybox =
            Skybox.Builder().color(red, green, blue, alpha)
                .build(modelViewer.engine)
        modelViewer.scene.skybox = skybox
        return Resource.Success("Loaded environment successfully from color")
    }

    fun setTransparentEnvironment() {
        modelViewer.scene.skybox = null
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: EnvironmentManger? = null

        fun getInstance(
            modelViewer: ModelViewer,
            context: Context,
            flutterAssets: FlutterPlugin.FlutterAssets
        ): EnvironmentManger =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: EnvironmentManger(modelViewer, context, flutterAssets).also {
                    INSTANCE = it
                }
            }

    }

}