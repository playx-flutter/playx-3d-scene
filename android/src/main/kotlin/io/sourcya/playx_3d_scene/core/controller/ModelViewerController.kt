package io.sourcya.playx_3d_scene.core.controller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Choreographer
import android.view.SurfaceView
import com.google.android.filament.Engine
import com.google.android.filament.View
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_3d_scene.core.animation.AnimationManger
import io.sourcya.playx_3d_scene.core.environment.EnvironmentManger
import io.sourcya.playx_3d_scene.core.light.LightManger
import io.sourcya.playx_3d_scene.core.loader.GlbLoader
import io.sourcya.playx_3d_scene.core.loader.GltfLoader
import io.sourcya.playx_3d_scene.core.models.ModelState
import io.sourcya.playx_3d_scene.core.models.model.Animation
import io.sourcya.playx_3d_scene.core.models.model.GlbModel
import io.sourcya.playx_3d_scene.core.models.model.GltfModel
import io.sourcya.playx_3d_scene.core.models.model.Model
import io.sourcya.playx_3d_scene.core.models.scene.Scene
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * This is the main class to handle filament engine.
 * and provide the model viewer for a surface view.
 */
class ModelViewerController constructor(
    private val context: Context,
    private var engine: Engine,
    private val flutterAssets: FlutterAssets,
    private val scene: Scene?,
    private val model: Model?,

    ) {
    private var modelViewer: CustomModelViewer? = null
    private val choreographer: Choreographer = Choreographer.getInstance()

    private var modelJob: Job? = null
    private var glbModelStateJob: Job? = null
    private var gltfModelStateJob: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var currentAnimationIndex: Int? = null

    private val surfaceView: SurfaceView = SurfaceView(context)

    private lateinit var glbLoader: GlbLoader

    private lateinit var gltfLoader: GltfLoader

    private lateinit var lightManger: LightManger

    private lateinit var environmentManger: EnvironmentManger

    private lateinit var animationManger: AnimationManger

    var modelState: MutableStateFlow<ModelState> = MutableStateFlow(ModelState.NONE)


    init {
        setUpViewer()

        setUpEnvironment()
        setUpLight()
        setUpLoadingModel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpViewer() {
        modelViewer = CustomModelViewer(surfaceView, engine)

        surfaceView.setOnTouchListener(modelViewer)
        surfaceView.setZOrderOnTop(true) // necessary


        val view = modelViewer?.view

        view?.let {
            // on mobile, better use lower quality color buffer
            view.renderQuality = view.renderQuality.apply {
                hdrColorBuffer = View.QualityLevel.MEDIUM
            }

            // dynamic resolution often helps a lot
            view.dynamicResolutionOptions = view.dynamicResolutionOptions.apply {
                enabled = true
                quality = View.QualityLevel.MEDIUM
            }

            // MSAA is needed with dynamic resolution MEDIUM
            view.multiSampleAntiAliasingOptions = view.multiSampleAntiAliasingOptions.apply {
                enabled = true
            }

            // FXAA is pretty cheap and helps a lot
            view.antiAliasing = View.AntiAliasing.FXAA

            // ambient occlusion is the cheapest effect that adds a lot of quality
            view.ambientOcclusionOptions = view.ambientOcclusionOptions.apply {
                enabled = true
            }
        }


        glbLoader = GlbLoader(modelViewer, context, flutterAssets)

        gltfLoader = GltfLoader(modelViewer, context, flutterAssets)

        lightManger = LightManger(modelViewer, context, flutterAssets)

        environmentManger = EnvironmentManger(modelViewer, context, flutterAssets)

        animationManger = AnimationManger(modelViewer, context)


//        // bloom is pretty expensive but adds a fair amount of realism
//        view.bloomOptions = view.bloomOptions.apply {
//            enabled = true
//        }


    }

    private fun setUpLoadingModel() {
        modelJob = coroutineScope.launch {
            val result = loadModel(model)
            Timber.d("Model loading result : ${result?.data} error :${result?.message}")

            if(result!= null && model?.fallback != null) {
                if(result is Resource.Error){
                    loadModel(model.fallback)
                    setUpAnimation(model.fallback.animation)

                }else{
                    setUpAnimation(model.animation)
                }
            }else{
                setUpAnimation(model?.animation)
            }
        }

    }

    private suspend fun loadModel(model : Model? ): Resource<String>? {
        var result :Resource<String>? = null
        when (model) {
            is GlbModel -> {
                if (!model.assetPath.isNullOrEmpty()) {
                    result = glbLoader.loadGlbFromAsset(model.assetPath)
                } else if (!model.url.isNullOrEmpty()) {
                    result = glbLoader.loadGlbFromUrl(model.url)
                }
            }
            is GltfModel -> {
                if (!model.assetPath.isNullOrEmpty()) {
                    result = gltfLoader.loadGltfFromAsset(
                        model.assetPath,
                        model.pathPrefix,
                        model.pathPostfix
                    )
                } else if (!model.url.isNullOrEmpty()) {
                    result = gltfLoader.loadGltfFromUrl(model.url,model.pathPrefix,model.pathPostfix)
                }
            }
            else -> {}
        }
        return result
    }
    private fun setUpLight() {

        coroutineScope.launch {
            val light = scene?.light
            if (!light?.assetPath.isNullOrEmpty()) {
                lightManger.setIndirectLightFromAsset(
                    light?.assetPath ?: "",
                    light?.intensity
                )
            } else if (!light?.url.isNullOrEmpty()) {
                lightManger.setIndirectLightFromAsset(
                    light?.url ?: "",
                    light?.intensity
                )
            } else if (light?.intensity != null) {
                lightManger.setIndirectLight(intensity = light.intensity)
            } else {
                lightManger.setDefaultLight()
            }
        }
    }

    private fun setUpEnvironment() {
        coroutineScope.launch {
            val skybox = scene?.skybox
            if (!skybox?.assetPath.isNullOrEmpty()) {
                environmentManger.setEnvironmentFromAsset(skybox?.assetPath ?: "")
            } else if (!skybox?.url.isNullOrEmpty()) {
                environmentManger.setEnvironmentFromAsset(skybox?.url ?: "")
            } else if (skybox?.color != null) {
                environmentManger.setEnvironmentFromColor(skybox.color)
            } else {
                environmentManger.setDefaultEnvironment()
                makeSurfaceViewTransparent()
            }
        }
    }


    private fun setUpAnimation(animation : Animation?) {
        if (animation?.autoPlay == true) {
            if (animation.index != null) {
                currentAnimationIndex = animation.index.toInt()
            } else if (!animation.name.isNullOrEmpty()) {
                currentAnimationIndex = animationManger.getAnimationIndexByName(animation.name)
            }
        } else {
            currentAnimationIndex = null
        }
    }


    private fun makeSurfaceViewTransparent() {
        modelViewer?.let {
            it.view.blendMode = View.BlendMode.TRANSLUCENT
            surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
            val options = it.renderer.clearOptions
            options.clear = true
            it.renderer.clearOptions = options
        }


    }

    private fun makeSurfaceViewNotTransparent() {
        modelViewer?.view?.blendMode = View.BlendMode.OPAQUE
        surfaceView.setZOrderOnTop(true) // necessary
        surfaceView.holder.setFormat(PixelFormat.OPAQUE)

    }


    fun getAnimationCount(): Int {
        return animationManger.getAnimationCount()
    }

    fun changeAnimation(animationIndex: Int?): Resource<Int> {
        return if (animationIndex == null) {
            Resource.Error("Animation index is not available")
        } else if (animationIndex >= getAnimationCount() || animationIndex < 0) {
            Resource.Error("Animation index is not valid")
        } else {
            currentAnimationIndex = animationIndex
            Resource.Success(animationIndex.toInt())
        }
    }


    fun changeAnimationByName(animationName: String?): Resource<Int> {
        return if (animationName.isNullOrEmpty()) {
            Resource.Error("Animation name is not valid")
        } else {
            val animationIndex = animationManger.getAnimationIndexByName(animationName)
            if (animationIndex == -1) {
                Resource.Error("Couldn't find animation with name $animationName")
            } else {
                currentAnimationIndex = animationIndex
                Resource.Success(animationIndex)
            }
        }

    }
    fun getAnimationNames() = animationManger.getAnimationNames()

    fun getCurrentAnimationIndex() = currentAnimationIndex


    fun getAnimationNameByIndex(index: Int?): Resource<String> {
        return if (index == null) {
            Resource.Error("Animation index is not available")
        } else if (index >= getAnimationCount() || index < 0) {
            Resource.Error("Animation index is not valid")
        } else {
            val name = animationManger.getAnimationNameByIndex(index)
            if (name.isNullOrEmpty()) {
                Resource.Error("Couldn't find animation name with index $index")
            } else {
                Resource.Success(name)
            }
        }

    }


    suspend fun changeEnvironment(assetPath: String?): Resource<String> {

        removeFrameCallback()
        val resource = environmentManger.setEnvironmentFromAsset(assetPath)
        if (resource is Resource.Success) {
            makeSurfaceViewNotTransparent()
            scene?.skybox?.assetPath = assetPath
        }
        addFrameCallback()
        return resource
    }


    fun changeEnvironmentColor(color: Int?): Resource<String> {

        removeFrameCallback()
        val resource = environmentManger.setEnvironmentFromColor(color)
        if (resource is Resource.Success) {
            scene?.skybox?.color = color
            makeSurfaceViewNotTransparent()
        }
        addFrameCallback()
        return resource
    }

    fun changeToTransparentEnvironment() {
        removeFrameCallback()
        environmentManger.setTransparentEnvironment()
        makeSurfaceViewTransparent()
        addFrameCallback()
    }

    suspend fun changeLight(assetPath: String?, intensity: Double? = null): Resource<String> {

        removeFrameCallback()
        val resource = lightManger.setIndirectLightFromAsset(assetPath, intensity)
        if (resource is Resource.Success) {
            scene?.light?.assetPath = assetPath
            scene?.light?.intensity = intensity
        }
        addFrameCallback()
        return resource

    }

    fun changeLight(intensity: Double?): Resource<String> {

        return if (intensity != null) {
            removeFrameCallback()
            scene?.light?.intensity = intensity
            lightManger.setIndirectLight(intensity)
            addFrameCallback()
            Resource.Success("changed light Intensity successfully")
        } else {
            Resource.Error("light Intensity is invalid")
        }

    }


    fun changeToDefaultLight() {

        removeFrameCallback()
        scene?.light?.intensity = LightManger.DEFAULT_LIGHT_INTENSITY
        lightManger.setDefaultLight()
        addFrameCallback()

    }


    suspend fun loadGlbModelFromAssets(assetPath: String?): Resource<String> {

        removeFrameCallback()
        modelJob?.cancel()
        val resource = glbLoader.loadGlbFromAsset(assetPath)
        addFrameCallback()
        return resource
    }

    suspend fun loadGlbModelFromUrl(url: String?): Resource<String> {

        removeFrameCallback()
        modelJob?.cancel()
        val resource = glbLoader.loadGlbFromUrl(url)
        addFrameCallback()
        return resource
    }

    suspend fun loadGltfModelFromAssets(
        assetPath: String?,
        gltfImagePathPrefix: String = "",
        gltfImagePathPostfix: String = ""
    ): Resource<String> {

        removeFrameCallback()
        modelJob?.cancel()

        val resource =
            gltfLoader.loadGltfFromAsset(assetPath, gltfImagePathPrefix, gltfImagePathPostfix)
        addFrameCallback()
        return resource

    }


    private fun setUpModelLoading() {
        glbModelStateJob = coroutineScope.launch {
            glbLoader.state.collectLatest {
                Timber.d("My Playx3dScenePlugin  setUpModelLoading : $it")
                modelState.value = it
            }
        }

        gltfModelStateJob = coroutineScope.launch {
            gltfLoader.state.collectLatest {
                modelState.value = it
            }
        }


    }


    private val frameCallback = object : Choreographer.FrameCallback {
        private val startTime = System.nanoTime()

        override fun doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            choreographer.postFrameCallback(this)
            if (currentAnimationIndex != null) {
                animationManger.showAnimation(currentAnimationIndex ?: -1, seconds)
            }
            try {
                modelViewer?.render(currentTime)
            } catch (_: Exception) {
            }
        }
    }


    fun handleOnResume() {
        surfaceView.invalidate()
        surfaceView.setZOrderOnTop(true)
        addFrameCallback()
        setUpModelLoading()
    }

    fun handleOnPause() {
        removeFrameCallback()
        glbModelStateJob?.cancel()
        glbModelStateJob = null
        gltfModelStateJob?.cancel()
        gltfModelStateJob = null
    }

    fun getView() = surfaceView

    private fun removeFrameCallback() {
        choreographer.removeFrameCallback(frameCallback)

    }

    private fun addFrameCallback() {
        choreographer.postFrameCallback(frameCallback)

    }

    fun destroy() {
        removeFrameCallback()
        modelJob?.cancel()
        glbModelStateJob?.cancel()
        modelJob = null
        glbModelStateJob = null
        gltfModelStateJob?.cancel()
        gltfModelStateJob = null

        modelViewer?.destroy()
    }


}

