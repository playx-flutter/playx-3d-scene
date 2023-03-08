package io.sourcya.playx_3d_scene.core.light

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.filament.IndirectLight
import com.google.android.filament.utils.HDRLoader
import com.google.android.filament.utils.KTX1Loader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_3d_scene.core.models.scene.Light
import io.sourcya.playx_3d_scene.core.models.states.SceneState
import io.sourcya.playx_3d_scene.core.network.NetworkClient
import io.sourcya.playx_3d_scene.core.utils.IBLProfiler
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.readAsset
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.nio.Buffer

internal class LightManger constructor(
    private val modelViewer: CustomModelViewer,
    private val iblPrefilter: IBLProfiler,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets

) {

    fun setDefaultLight() {
        modelViewer.setLightState(SceneState.LOADING)
        setIndirectLight(io.sourcya.playx_3d_scene.core.models.scene.IndirectLight(intensity = DEFAULT_LIGHT_INTENSITY))
        modelViewer.setLightState(SceneState.LOADED)

    }

    suspend fun setIndirectLightFromKtxAsset(path: String?, intensity: Double?): Resource<String> {
        modelViewer.setLightState(SceneState.LOADING)
        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        val light = KTX1Loader.createIndirectLight(modelViewer.engine, it)
                        light.intensity = intensity?.toFloat() ?: 50_000f

                        withContext(Dispatchers.Main) {
                            modelViewer.scene.indirectLight = light
                        }
                    }
                    modelViewer.setLightState(SceneState.LOADED)
                    return@withContext Resource.Success("changed Light successfully from ${path ?: ""}")
                }
                is Resource.Error -> {
                    modelViewer.setLightState(SceneState.ERROR)

                    return@withContext Resource.Error(
                        bufferResource.message ?: "Couldn't changed Light from asset"
                    )
                }

            }
        }
    }

    suspend fun setIndirectLightFromKtxUrl(url: String?, intensity: Double?): Resource<String> {
        modelViewer.setLightState(SceneState.LOADING)
        if (url.isNullOrEmpty()) {
            modelViewer.setLightState(SceneState.ERROR)
            return Resource.Error(
                "url is empty"
            )
        }

        return withContext(Dispatchers.IO) {

            val buffer = NetworkClient.downloadFile(url)

            if (buffer != null) {
                val light = KTX1Loader.createIndirectLight(modelViewer.engine, buffer)
                light.intensity = intensity?.toFloat() ?: 50_000f

                withContext(Dispatchers.Main) {
                    modelViewer.scene.indirectLight = light
                }

                modelViewer.setLightState(SceneState.LOADED)
                return@withContext Resource.Success("changed Light successfully from $url")
            } else {
                modelViewer.setLightState(SceneState.ERROR)
                return@withContext Resource.Error(
                    "Couldn't load Light from $url"
                )
            }
        }
    }


    suspend fun setIndirectLightFromHdrAsset(path: String?, intensity: Double?): Resource<String> {
        modelViewer.setLightState(SceneState.LOADING)
        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    val buffer = bufferResource.data
                    if (buffer != null) {
                        return@withContext loadIndirectLightHdrFromBuffer(buffer, intensity)
                    } else {
                        modelViewer.setLightState(SceneState.ERROR)
                        return@withContext Resource.Error("Couldn't changed Light from asset")
                    }
                }
                is Resource.Error -> {
                    modelViewer.setLightState(SceneState.ERROR)

                    return@withContext Resource.Error(
                        bufferResource.message ?: "Couldn't changed Light from asset"
                    )
                }

            }
        }
    }

    suspend fun setIndirectLightFromHdrUrl(url: String?, intensity: Double?): Resource<String> {
        modelViewer.setLightState(SceneState.LOADING)
        if (url.isNullOrEmpty()) {
            modelViewer.setLightState(SceneState.ERROR)
            return Resource.Error(
                "url is empty"
            )
        }

        return withContext(Dispatchers.IO) {

            val buffer = NetworkClient.downloadFile(url)

            if (buffer != null) {

                return@withContext loadIndirectLightHdrFromBuffer(buffer, intensity)
            } else {
                modelViewer.setLightState(SceneState.ERROR)
                return@withContext Resource.Error(
                    "Couldn't load Light from $url"
                )
            }
        }
    }


    fun setIndirectLight(light: Light? ) :Resource<String>{
        modelViewer.setLightState(SceneState.LOADING)
        Timber.d("setIndirectLight : $light")

        if(light == null) {
            modelViewer.setLightState(SceneState.ERROR)
            return Resource.Error("Light is null")

        }
        try {
            val builder = IndirectLight.Builder().apply {
                light.intensity?.toFloat()?.let {
                    this.intensity(it)
                }

                if (light.radianceBands != null && light.radianceSh != null) {
                    this.radiance(light.radianceBands, light.radianceSh)
                }

                if (light.irradianceBands != null && light.irradianceSh != null) {
                    this.irradiance(light.irradianceBands, light.irradianceSh)
                }

                light.rotation?.let {
                    this.rotation(it)
                }
            }

            modelViewer.scene.indirectLight = builder.build(modelViewer.engine)
            modelViewer.setLightState(SceneState.LOADED)
            return Resource.Success("changed Light successfully")
        }catch (t:Throwable){
            modelViewer.setLightState(SceneState.ERROR)
            return Resource.Error(t.message?: "Couldn't change Light")
        }

    }


    private suspend fun loadIndirectLightHdrFromBuffer(
        buffer: Buffer,
        intensity: Double?
    ): Resource<String> {
        modelViewer.setLightState(SceneState.LOADING)

        return withContext(Dispatchers.Main) {
            val engine = modelViewer.engine

            try {
                val texture = HDRLoader.createTexture(engine, buffer)
                if (texture == null) {
                    modelViewer.setLightState(SceneState.ERROR)
                    return@withContext Resource.Error("Could not decode HDR file")
                } else {

                    val skyboxTexture = iblPrefilter.createCubeMapTexture(texture)

                    engine.destroyTexture(texture)

                    val reflections = iblPrefilter.getLightReflection(skyboxTexture)


                    val ibl = IndirectLight.Builder()
                        .reflections(reflections)
                        .intensity(intensity?.toFloat() ?:30_000f)
                        .build(engine)

                    // destroy the previous IBl
                    modelViewer.destroyIndirectLight()

                    modelViewer.scene.indirectLight = ibl
                    modelViewer.setLightState(SceneState.LOADED)

                    return@withContext Resource.Success("loaded Indirect light successfully")
                }
            } catch (e: Throwable) {
                modelViewer.setLightState(SceneState.ERROR)
                return@withContext Resource.Error("Could not decode HDR file")

            }
        }
    }



    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LightManger? = null
        const val DEFAULT_LIGHT_INTENSITY = 30_000.0

        fun getInstance(
            modelViewer: CustomModelViewer,
            context: Context,
            iblPrefilter: IBLProfiler,
            flutterAssets: FlutterPlugin.FlutterAssets
        ): LightManger =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: LightManger(modelViewer, iblPrefilter,context, flutterAssets).also {
                    INSTANCE = it
                }
            }

    }
}