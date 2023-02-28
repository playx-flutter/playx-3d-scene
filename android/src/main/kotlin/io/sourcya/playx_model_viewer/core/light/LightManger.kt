package io.sourcya.playx_model_viewer.core.light

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.filament.IndirectLight
import com.google.android.filament.utils.KTX1Loader
import com.google.android.filament.utils.ModelViewer
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_model_viewer.core.utils.readAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LightManger private constructor(
    private val modelViewer: ModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets

) {


    suspend fun setDefaultLight() {
        setIndirectLight(40_000.0)
    }

    suspend fun setIndirectLightFromAsset(path: String, intensity: Double? ) {
        withContext(Dispatchers.IO) {
            val buffer = readAsset(path, flutterAssets, context)
            buffer?.let {
                val light = KTX1Loader.createIndirectLight(modelViewer.engine, it)
                light.intensity = intensity?.toFloat() ?:50_000f

                withContext(Dispatchers.Main) {
                    modelViewer.scene.indirectLight = light
                }
            }
        }
    }


    suspend fun setIndirectLight(
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

        fun getInstance(modelViewer: ModelViewer, context: Context,  flutterAssets : FlutterPlugin.FlutterAssets): LightManger =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: LightManger(modelViewer, context,flutterAssets).also {
                    INSTANCE = it
                }
            }

    }
}