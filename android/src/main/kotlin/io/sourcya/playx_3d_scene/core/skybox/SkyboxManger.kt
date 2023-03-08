package io.sourcya.playx_3d_scene.core.skybox

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import com.google.android.filament.IndirectLight
import com.google.android.filament.Skybox
import com.google.android.filament.utils.HDRLoader
import com.google.android.filament.utils.KTX1Loader
import io.flutter.embedding.engine.plugins.FlutterPlugin
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

internal class SkyboxManger constructor(
    private val modelViewer: CustomModelViewer,
    private val iblProfiler: IBLProfiler,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets

) {


    fun setDefaultSkybox() {
        modelViewer.setSkyboxState(SceneState.LOADING)
        setTransparentSkybox()
        modelViewer.setSkyboxState(SceneState.LOADED)

    }

    suspend fun setSkyboxFromKTXAsset(path: String?): Resource<String> {

        modelViewer.setSkyboxState(SceneState.LOADING)
        modelViewer.destroyModel()
        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        val skybox =
                            KTX1Loader.createSkybox(modelViewer.engine, it)

                        withContext(Dispatchers.Main) {
                            modelViewer.destroySkybox()

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

    suspend fun setSkyboxFromKTXUrl(url: String?): Resource<String> {
        modelViewer.setSkyboxState(SceneState.LOADING)

        if (url.isNullOrEmpty()) return Resource.Error("URL is empty")

        return withContext(Dispatchers.IO) {
            val buffer = NetworkClient.downloadFile(url)
            if (buffer != null) {
                val skybox =
                    KTX1Loader.createSkybox(modelViewer.engine, buffer)
                withContext(Dispatchers.Main) {
                    modelViewer.destroySkybox()

                    modelViewer.scene.skybox = skybox
                }
                modelViewer.setSkyboxState(SceneState.LOADED)
                return@withContext Resource.Success("Loaded skybox successfully from $url")

            } else {
                modelViewer.setSkyboxState(SceneState.ERROR)
                return@withContext Resource.Error(
                    "Couldn't load skybox form $url"
                )
            }
        }
    }


    suspend fun setSkyboxFromHdrAsset(path: String?,showSun:Boolean=false,shouldUpdateLight:Boolean =false, intensity:Double?= null): Resource<String> {

        modelViewer.setSkyboxState(SceneState.LOADING)
        Timber.d("loading hdr skybox  loading :$path")

        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {

                is Resource.Success -> {
                    val buffer = bufferResource.data
                    return@withContext loadSkyboxFromHdrBuffer(buffer,showSun,shouldUpdateLight,intensity)
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


    @SuppressLint("LogNotTimber")
    suspend fun setSkyboxFromHdrUrl(url: String?,showSun:Boolean=false,shouldUpdateLight:Boolean =false, intensity:Double?= null): Resource<String> {

        modelViewer.setSkyboxState(SceneState.LOADING)
        Timber.d("loading hdr skybox buffer loading :$url")

        if(url.isNullOrEmpty()) {
            modelViewer.setSkyboxState(SceneState.ERROR)
            return Resource.Error("URL is empty")
        }
        return withContext(Dispatchers.IO) {
            val buffer = NetworkClient.downloadFile(url)

            if(buffer != null) {
                Timber.d("loading hdr skybox buffer downloaded")
                return@withContext loadSkyboxFromHdrBuffer(buffer,showSun,shouldUpdateLight,intensity)
            }else{
                 modelViewer.setSkyboxState(SceneState.ERROR)
                    return@withContext Resource.Error(
                         "Couldn't load HDR file from URL"
                    )
                }
            }

        }


    private suspend fun loadSkyboxFromHdrBuffer(buffer: Buffer?,showSun:Boolean=false, shouldUpdateLight:Boolean =false, intensity:Double?= null) :Resource<String>{

       return withContext(Dispatchers.Main) {

        val engine = modelViewer.engine
        try {

            val texture = buffer?.let { HDRLoader.createTexture(engine, it) }

            if (texture != null) {
                val skyboxTexture = iblProfiler.createCubeMapTexture(texture)
                engine.destroyTexture(texture)
                skyboxTexture.let {
                    val sky = Skybox.Builder()
                        .environment(skyboxTexture)
                        .showSun(showSun)
                        .build(engine)


                    // updates scene light with skybox when loaded with same hdr file
                    if(shouldUpdateLight) {
                        val reflections = iblProfiler.getLightReflection(skyboxTexture)
                        val ibl = IndirectLight.Builder()
                            .reflections(reflections)
                            .intensity(intensity?.toFloat() ?: 30_000f)
                            .build(engine)
                        // destroy the previous IBl
                        modelViewer.destroyIndirectLight()
                        modelViewer.scene.indirectLight = ibl
                        modelViewer.setLightState(SceneState.LOADED)
                    }




                    modelViewer.destroySkybox()
                    modelViewer.scene.skybox = sky



                }


                modelViewer.setSkyboxState(SceneState.LOADED)

                return@withContext Resource.Success("Loaded hdr skybox successfully }")
            } else {
                modelViewer.setSkyboxState(SceneState.ERROR)
                return@withContext Resource.Error("Could not decode HDR file")
            }

        } catch (e: Throwable){
            modelViewer.setSkyboxState(SceneState.ERROR)
            return@withContext Resource.Error(
                "Could not decode HDR file ${e.message}"
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
            iblProfiler:IBLProfiler,
            context: Context,
            flutterAssets: FlutterPlugin.FlutterAssets
        ): SkyboxManger =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SkyboxManger(modelViewer,iblProfiler, context, flutterAssets).also {
                    INSTANCE = it
                }
            }

    }

}