package io.sourcya.playx_model_viewer.core.light

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.filament.IndirectLight
import com.google.android.filament.utils.KTX1Loader
import com.google.android.filament.utils.ModelViewer
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_model_viewer.core.utils.Resource
import io.sourcya.playx_model_viewer.core.utils.readAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class LightManger private constructor(
    private val modelViewer: ModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets

) {

    fun setDefaultLight() {
        setIndirectLight(DEFAULT_LIGHT_INTENSITY)
    }
    suspend fun setIndirectLightFromAsset(path: String?, intensity: Double? ): Resource<String> {
        return   withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        val light = KTX1Loader.createIndirectLight(modelViewer.engine, it)
                        light.intensity = intensity?.toFloat() ?: 50_000f

                        withContext(Dispatchers.Main) {
                            modelViewer.scene.indirectLight = light
                        }
                    }
                        return@withContext  Resource.Success("changed Light successfully from ${path?:""}")
                    }
                    is Resource.Error -> {
                        return@withContext Resource.Error(bufferResource.message ?:"Couldn't changed Light from asset")
                    }

                }
        }
    }


    fun setIndirectLight(
        intensity: Double?,
        radianceBands: Int = 1,
        radianceSh: FloatArray = floatArrayOf(1f, 1f, 1f),
        irradianceBands: Int = 1,
        irradianceSh: FloatArray = floatArrayOf(1f, 1f, 1f),
    ) {
        modelViewer.scene.indirectLight = IndirectLight.Builder()
            .intensity(intensity?.toFloat() ?:50_000f)
            .radiance(radianceBands, radianceSh)
            .irradiance(irradianceBands, irradianceSh)
            .build(modelViewer.engine)
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LightManger? = null
        const val DEFAULT_LIGHT_INTENSITY = 40_000.0

        fun getInstance(modelViewer: ModelViewer, context: Context,  flutterAssets : FlutterPlugin.FlutterAssets): LightManger =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: LightManger(modelViewer, context,flutterAssets).also {
                    INSTANCE = it
                }
            }

    }
}