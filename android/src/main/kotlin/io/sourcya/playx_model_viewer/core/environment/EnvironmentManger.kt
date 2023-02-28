package io.sourcya.playx_model_viewer.core.environment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KTX1Loader
import com.google.android.filament.utils.ModelViewer
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_model_viewer.core.utils.readAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EnvironmentManger private constructor(
    private val modelViewer: ModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets

) {


    suspend fun setDefaultEnvironment() {
        setTransparentEnvironment()
    }

    suspend fun setTestEnvironment() {
        setEnvironmentFromAsset("envs/venetian_crossroads_2k/venetian_crossroads_2k_skybox.ktx")

    }

    suspend fun setEnvironmentFromAsset(path: String) {
        withContext(Dispatchers.IO) {
            val buffer = readAsset(path, flutterAssets , context)
            buffer?.let {
                val skybox = KTX1Loader.createSkybox(modelViewer.engine, it)
                withContext(Dispatchers.Main) {
                    modelViewer.scene.skybox = skybox
                }
            }
        }
    }

    suspend fun setEnvironmentFromColor(r: Float, g: Float, b: Float, a: Float) {
        val skybox = Skybox.Builder().color(r, g, b, a).build(modelViewer.engine)
        modelViewer.scene.skybox = skybox
    }

    suspend fun setEnvironmentFromColor(color: Int?) {
        if (color == null) return
        val red: Float = Color.red(color) / 255f
        val green: Float = Color.green(color) / 255f
        val blue: Float = Color.blue(color) / 255f
        val alpha: Float = Color.alpha(color) / 255f

        val skybox =
            Skybox.Builder().color(red, green, blue, alpha)
                .build(modelViewer.engine)
        modelViewer.scene.skybox = skybox
    }

    fun setTransparentEnvironment() {
        modelViewer.scene.skybox = null
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: EnvironmentManger? = null

        fun getInstance(modelViewer: ModelViewer, context: Context,  flutterAssets : FlutterPlugin.FlutterAssets): EnvironmentManger =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: EnvironmentManger(modelViewer, context,flutterAssets).also {
                    INSTANCE = it
                }
            }

    }

}