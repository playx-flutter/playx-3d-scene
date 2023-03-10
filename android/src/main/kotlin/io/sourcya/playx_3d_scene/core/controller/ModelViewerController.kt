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
import io.sourcya.playx_3d_scene.core.skybox.SkyboxManger
import io.sourcya.playx_3d_scene.core.light.IndirectLightManger
import io.sourcya.playx_3d_scene.core.light.LightManger
import io.sourcya.playx_3d_scene.core.loader.GlbLoader
import io.sourcya.playx_3d_scene.core.loader.GltfLoader
import io.sourcya.playx_3d_scene.core.models.states.ModelState
import io.sourcya.playx_3d_scene.core.models.model.Animation
import io.sourcya.playx_3d_scene.core.models.model.GlbModel
import io.sourcya.playx_3d_scene.core.models.model.GltfModel
import io.sourcya.playx_3d_scene.core.models.model.Model
import io.sourcya.playx_3d_scene.core.models.scene.*
import io.sourcya.playx_3d_scene.core.models.scene.light.DefaultIndirectLight
import io.sourcya.playx_3d_scene.core.models.scene.light.HdrIndirectLight
import io.sourcya.playx_3d_scene.core.models.scene.light.KtxIndirectLight
import io.sourcya.playx_3d_scene.core.models.states.SceneState
import io.sourcya.playx_3d_scene.core.models.states.SceneState.Companion.getSceneState
import io.sourcya.playx_3d_scene.core.utils.IBLProfiler
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
    private val iblProfiler: IBLProfiler,
    private val flutterAssets: FlutterAssets,
    private val scene: Scene?,
    private val model: Model?,

    ) {
    private lateinit var modelViewer: CustomModelViewer
    private val choreographer: Choreographer = Choreographer.getInstance()

    private var modelJob: Job? = null
    private var glbModelStateJob: Job? = null
    private var sceneStateJob: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var currentAnimationIndex: Int? = null

    private val surfaceView: SurfaceView = SurfaceView(context)

    private lateinit var glbLoader: GlbLoader

    private lateinit var gltfLoader: GltfLoader

    private lateinit var lightManger:LightManger
    private lateinit var indirectLightManger: IndirectLightManger

    private lateinit var skyboxManger: SkyboxManger

    private lateinit var animationManger: AnimationManger

    val modelState: MutableStateFlow<ModelState> = MutableStateFlow(ModelState.NONE)
    val sceneState: MutableStateFlow<SceneState> = MutableStateFlow(SceneState.NONE)


    init {
        setUpViewer()
        setUpSkybox()
        setUpLight()
        setUpIndirectLight()
        setUpLoadingModel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpViewer() {
        modelViewer = CustomModelViewer(surfaceView, engine)

        surfaceView.setOnTouchListener(modelViewer)
        surfaceView.setZOrderOnTop(true) // necessary


        val view = modelViewer.view

        view.let {
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


        glbLoader = GlbLoader.getInstance(modelViewer, context, flutterAssets)

        gltfLoader = GltfLoader.getInstance(modelViewer, context, flutterAssets)

        lightManger = LightManger(modelViewer)
        indirectLightManger = IndirectLightManger(modelViewer, iblProfiler, context, flutterAssets)

        skyboxManger = SkyboxManger(modelViewer, iblProfiler, context, flutterAssets)

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

            if (result != null && model?.fallback != null) {
                if (result is Resource.Error) {
                    loadModel(model.fallback)
                    setUpAnimation(model.fallback.animation)

                } else {
                    setUpAnimation(model.animation)
                }
            } else {
                setUpAnimation(model?.animation)
            }
        }

    }

    private suspend fun loadModel(model: Model?): Resource<String>? {
        var result: Resource<String>? = null
        when (model) {
            is GlbModel -> {
                if (!model.assetPath.isNullOrEmpty()) {
                    result = glbLoader.loadGlbFromAsset(model.assetPath,model.scale,model.centerPosition)
                } else if (!model.url.isNullOrEmpty()) {
                    result = glbLoader.loadGlbFromUrl(model.url,model.scale,model.centerPosition)
                }
            }
            is GltfModel -> {
                if (!model.assetPath.isNullOrEmpty()) {
                    result = gltfLoader.loadGltfFromAsset(
                        model.assetPath,
                        model.pathPrefix,
                        model.pathPostfix,
                        model.scale
                        ,model.centerPosition
                    )
                } else if (!model.url.isNullOrEmpty()) {
                    result =
                        gltfLoader.loadGltfFromUrl(model.url, model.pathPrefix, model.pathPostfix,model.scale,model.centerPosition)
                }
            }
            else -> {}
        }
        return result
    }


    private fun setUpLight(){
        val light= scene?.light
        Timber.d("setUpLight : $light")
        if(light!=null){
            lightManger.changeLight(light)
        }else{
            lightManger.setDefaultLight()
        }

    }
    private fun setUpIndirectLight() {

        coroutineScope.launch {
            val light = scene?.indirectLight

            if (light == null) {
                indirectLightManger.setDefaultIndirectLight()
            } else {
                when (light) {
                    is KtxIndirectLight -> {
                        if (!light.assetPath.isNullOrEmpty()) {
                            indirectLightManger.setIndirectLightFromKtxAsset(
                                light.assetPath, light.intensity
                            )
                        } else if (!light.url.isNullOrEmpty()) {
                            indirectLightManger.setIndirectLightFromKtxUrl(light.url, light.intensity)
                        }
                    }
                    is HdrIndirectLight -> {

                        if (!light.assetPath.isNullOrEmpty()) {
                            val shouldUpdateLight = light.assetPath != scene?.skybox?.assetPath

                            if (shouldUpdateLight) {
                                indirectLightManger.setIndirectLightFromHdrAsset(
                                    light.assetPath, light.intensity
                                )
                            }

                        } else if (!light.url.isNullOrEmpty()) {
                            val shouldUpdateLight = light.url != scene?.skybox?.url
                            if (shouldUpdateLight) {
                                indirectLightManger.setIndirectLightFromHdrUrl(light.url, light.intensity)
                            }
                        }
                    }
                    else -> {
                        indirectLightManger.setIndirectLight(light)
                    }

                }


            }
        }
    }

    private fun setUpSkybox() {
        coroutineScope.launch {
            val skybox = scene?.skybox
            Timber.d("setUpSkybox Skybox : $skybox")
            if (skybox == null) {
                skyboxManger.setDefaultSkybox()
                makeSurfaceViewTransparent()
            } else {
                when (skybox) {
                    is KtxSkybox -> {
                        if (!skybox.assetPath.isNullOrEmpty()) {
                            skyboxManger.setSkyboxFromKTXAsset(skybox.assetPath)
                        } else if (!skybox.url.isNullOrEmpty()) {
                            skyboxManger.setSkyboxFromKTXUrl(skybox.url)
                        }
                    }
                    is HdrSkybox -> {

                        if (!skybox.assetPath.isNullOrEmpty()) {
                            val shouldUpdateLight = skybox.assetPath == scene?.indirectLight?.assetPath
                            skyboxManger.setSkyboxFromHdrAsset(
                                skybox.assetPath,
                                skybox.showSun ?: false,
                                shouldUpdateLight,
                                scene?.indirectLight?.intensity
                            )
                        } else if (!skybox.url.isNullOrEmpty()) {
                            val shouldUpdateLight = skybox.url == scene?.indirectLight?.url
                            skyboxManger.setSkyboxFromHdrUrl(
                                skybox.url,
                                skybox.showSun ?: false,
                                shouldUpdateLight,
                                scene?.indirectLight?.intensity
                            )
                        }
                    }
                    is ColoredSkybox -> {
                        if (skybox.color != null) {
                            skyboxManger.setSkyboxFromColor(skybox.color)
                        }
                    }

                }
            }
        }
    }


    private fun setUpAnimation(animation: Animation?) {
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
        modelViewer.let {
            it.view.blendMode = View.BlendMode.TRANSLUCENT
            surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
            val options = it.renderer.clearOptions
            options.clear = true
            it.renderer.clearOptions = options
        }


    }

    private fun makeSurfaceViewNotTransparent() {
        modelViewer.view.blendMode = View.BlendMode.OPAQUE
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


    suspend fun changeSkyboxFromKtxAsset(assetPath: String?): Resource<String> {
        removeFrameCallback()
        val resource = skyboxManger.setSkyboxFromKTXAsset(assetPath)
        if (resource is Resource.Success) {
            makeSurfaceViewNotTransparent()
            scene?.skybox?.assetPath = assetPath
        }
        addFrameCallback()
        return resource
    }


    suspend fun changeSkyboxFromKtxUrl(url: String?): Resource<String> {
        removeFrameCallback()
        val resource = skyboxManger.setSkyboxFromKTXUrl(url)
        if (resource is Resource.Success) {
            makeSurfaceViewNotTransparent()
            scene?.skybox?.url = url
        }
        addFrameCallback()
        return resource
    }

    suspend fun changeSkyboxFromHdrAsset(assetPath: String?): Resource<String> {
        removeFrameCallback()
        val resource = skyboxManger.setSkyboxFromHdrAsset(assetPath)
        if (resource is Resource.Success) {
            makeSurfaceViewNotTransparent()
            scene?.skybox?.assetPath = assetPath
        }
        addFrameCallback()
        return resource
    }


    suspend fun changeSkyboxFromHdrUrl(url: String?): Resource<String> {
        removeFrameCallback()
        val resource = skyboxManger.setSkyboxFromHdrUrl(url)
        if (resource is Resource.Success) {
            makeSurfaceViewNotTransparent()
            scene?.skybox?.url = url
        }
        addFrameCallback()
        return resource
    }


    fun changeSkyboxByColor(color: Int?): Resource<String> {

        removeFrameCallback()
        val resource = skyboxManger.setSkyboxFromColor(color)
        if (resource is Resource.Success) {
            scene?.skybox?.color = color
            makeSurfaceViewNotTransparent()
        }
        addFrameCallback()
        return resource
    }

    fun changeToTransparentSkybox() {
        removeFrameCallback()
        skyboxManger.setTransparentSkybox()
        makeSurfaceViewTransparent()
        addFrameCallback()
    }

    suspend fun changeIndirectLightFromKtxAsset(
        assetPath: String?,
        intensity: Double? = null
    ): Resource<String> {
        removeFrameCallback()
        val resource = indirectLightManger.setIndirectLightFromKtxAsset(assetPath, intensity)
        if (resource is Resource.Success) {
            scene?.indirectLight?.assetPath = assetPath
            scene?.indirectLight?.intensity = intensity
        }
        addFrameCallback()
        return resource

    }

    suspend fun changeIndirectLightFromKtxUrl(url: String?, intensity: Double? = null): Resource<String> {
        removeFrameCallback()
        val resource = indirectLightManger.setIndirectLightFromKtxUrl(url, intensity)
        if (resource is Resource.Success) {
            scene?.indirectLight?.url = url
            scene?.indirectLight?.intensity = intensity
        }
        addFrameCallback()
        return resource
    }

    suspend fun changeIndirectLightFromHdrAsset(
        assetPath: String?,
        intensity: Double? = null
    ): Resource<String> {
        removeFrameCallback()
        val resource = indirectLightManger.setIndirectLightFromHdrAsset(assetPath, intensity)
        if (resource is Resource.Success) {
            scene?.indirectLight?.assetPath = assetPath
            scene?.indirectLight?.intensity = intensity
        }
        addFrameCallback()
        return resource

    }

    suspend fun changeIndirectLightFromHdrUrl(url: String?, intensity: Double? = null): Resource<String> {
        removeFrameCallback()
        val resource = indirectLightManger.setIndirectLightFromHdrUrl(url, intensity)
        if (resource is Resource.Success) {
            scene?.indirectLight?.url = url
            scene?.indirectLight?.intensity = intensity
        }
        addFrameCallback()
        return resource
    }


    fun changeIndirectLightByDefaultIndirectLight(indirectLight: DefaultIndirectLight?): Resource<String> {

        removeFrameCallback()
        val result = indirectLightManger.setIndirectLight(indirectLight)
        if (result is Resource.Success) {
            scene?.indirectLight = indirectLight
        }
        addFrameCallback()

        return result

    }


    fun changeToDefaultIndirectLight() {

        removeFrameCallback()
        scene?.indirectLight?.intensity = IndirectLightManger.DEFAULT_LIGHT_INTENSITY
        indirectLightManger.setDefaultIndirectLight()
        addFrameCallback()

    }



    fun changeLight(light: Light?): Resource<String> {
        removeFrameCallback()

        val result= lightManger.changeLight(light)

        Timber.d("change scene Light:$light, ${result}")
        if(result is Resource.Success){
            scene?.light = light
        }
        addFrameCallback()
        return result
    }


    fun changeToDefaultLight(){
        removeFrameCallback()
        val result= lightManger.setDefaultLight()
        addFrameCallback()
        return result
    }


    suspend fun loadGlbModelFromAssets(assetPath: String?): Resource<String> {

        removeFrameCallback()
        modelJob?.cancel()
        val resource = glbLoader.loadGlbFromAsset(assetPath,model?.scale,model?.centerPosition)
        addFrameCallback()
        return resource
    }

    suspend fun loadGlbModelFromUrl(url: String?): Resource<String> {

        removeFrameCallback()
        modelJob?.cancel()
        val resource = glbLoader.loadGlbFromUrl(url,model?.scale,model?.centerPosition)
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
            gltfLoader.loadGltfFromAsset(assetPath, gltfImagePathPrefix, gltfImagePathPostfix,model?.scale,model?.centerPosition)
        addFrameCallback()
        return resource

    }


    fun changeModelScale(scale: Float?): Resource<String> {
        removeFrameCallback()
        if(scale == null){
            addFrameCallback()
          return  Resource.Error("Scale must be provided")
        }
        modelViewer.transformToUnitCube(model?.centerPosition,scale)
        model?.scale = scale
        addFrameCallback()
        return Resource.Success("Model scale has been changed successfully")

    }


    fun changeModelPosition(position: FloatArray?): Resource<String> {
        removeFrameCallback()
        if(position == null){
            addFrameCallback()
            return  Resource.Error("Center position must be provided")
        }
        modelViewer.transformToUnitCube(position,model?.scale)
        model?.centerPosition = position
        addFrameCallback()
        return Resource.Success("Model center position has been changed successfully")

    }


    private fun listenToModelState() {
        glbModelStateJob = coroutineScope.launch {
            modelViewer.currentModelState.collectLatest {
                Timber.d("My Playx3dScenePlugin  setUpModelLoading : $it")
                modelState.value = it
            }
        }
    }

    private fun listenToSceneState() {
        sceneStateJob = coroutineScope.launch {
            combine(
                modelViewer.currentSkyboxState,
                modelViewer.currentLightState
            ) { (skyboxState, lightState) ->
                getSceneState(skyboxState, lightState)
            }.collectLatest { state ->
                sceneState.value = state
            }
        }
    }

    /**
     *Flow that holds current frame in nanoseconds
     */
    fun getRenderStateFlow() = modelViewer.rendererStateFlow


    private val frameCallback = object : Choreographer.FrameCallback {
        private val startTime = System.nanoTime()

        override fun doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            choreographer.postFrameCallback(this)
            if (currentAnimationIndex != null) {
                animationManger.showAnimation(currentAnimationIndex ?: -1, seconds)
            }
            try {
                modelViewer.render(currentTime)
            } catch (_: Exception) {
            }
        }
    }


    fun handleOnResume() {
        Timber.d("My Playx3dScenePlugin  handleOnResume")
        listenToModelState()
        listenToSceneState()
        surfaceView.invalidate()
        surfaceView.setZOrderOnTop(true)
        addFrameCallback()
    }

    fun handleOnPause() {
        removeFrameCallback()
        glbModelStateJob?.cancel()
        glbModelStateJob = null
        sceneStateJob?.cancel()
        sceneStateJob = null
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
        modelJob = null
        glbModelStateJob?.cancel()
        glbModelStateJob = null
        sceneStateJob?.cancel()
        sceneStateJob = null
        lightManger.destroyLight()
        modelViewer.destroy()

    }


}

