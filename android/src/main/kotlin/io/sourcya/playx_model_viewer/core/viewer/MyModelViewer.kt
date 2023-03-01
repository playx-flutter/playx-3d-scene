package io.sourcya.playx_model_viewer.core.viewer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Choreographer
import android.view.SurfaceView
import com.google.android.filament.Engine
import com.google.android.filament.View
import com.google.android.filament.utils.Manipulator
import com.google.android.filament.utils.Mat4
import com.google.android.filament.utils.ModelViewer
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_model_viewer.core.animation.AnimationManger
import io.sourcya.playx_model_viewer.core.environment.EnvironmentManger
import io.sourcya.playx_model_viewer.core.light.LightManger
import io.sourcya.playx_model_viewer.core.loader.GlbLoader
import io.sourcya.playx_model_viewer.core.loader.GltfLoader
import io.sourcya.playx_model_viewer.core.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MyModelViewer constructor(
    private val context: Context,
    private val engine:Engine,
    private val flutterAssets: FlutterAssets,
    private val glbAssetPath: String? = null,
    private val glbUrl: String? = null,
    private val gltfAssetPath: String? = null,
    private val gltfImagePathPrefix: String = "",
    private val gltfImagePathPostfix: String = "",
    private var lightAssetPath: String? = null,
    private var lightIntensity: Double? = null,
    private var environmentAssetPath: String? = null,
    private var environmentColor: Int? = null,

    private val animationIndex: Int? = null,
    private val animationName: String? = null,
    private val autoPlay: Boolean = false,

    ) {
    private lateinit var modelViewer: ModelViewer
    private lateinit var choreographer: Choreographer

    private val glbLoader: GlbLoader by lazy {
        GlbLoader.getInstance(modelViewer, context, flutterAssets)
    }
    private val gltfLoader: GltfLoader by lazy {
        GltfLoader.getInstance(modelViewer, context, flutterAssets)
    }

    private val lightManger by lazy {
        LightManger.getInstance(modelViewer, context, flutterAssets)
    }

    private val environmentManger by lazy {
        EnvironmentManger.getInstance(modelViewer, context, flutterAssets)
    }

    private var modelJob :Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val animationManger: AnimationManger by lazy {
        AnimationManger.getInstance(modelViewer, context)
    }

    private var currentAnimationIndex: Int? = null


    private lateinit var manip: Manipulator

    lateinit var surfaceView: SurfaceView


    init {
        setUpViewer()
        setUpEnvironment()
        setUpLight()
        setUpLoadingModel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpViewer() {
        surfaceView = SurfaceView(context)

        choreographer = Choreographer.getInstance()

        modelViewer = ModelViewer(surfaceView,engine)


        surfaceView.setOnTouchListener(modelViewer)
        surfaceView.setZOrderOnTop(true) // necessary


        val view = modelViewer.view

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


//        // bloom is pretty expensive but adds a fair amount of realism
//        view.bloomOptions = view.bloomOptions.apply {
//            enabled = true
//        }


    }

    private fun setUpLoadingModel() {
        modelJob= coroutineScope.launch {
            if (!glbAssetPath.isNullOrEmpty()) {
                glbLoader.loadGlbFromAsset(glbAssetPath)
            } else if (!glbUrl.isNullOrEmpty()) {
                glbLoader.loadGlbFromUrl(glbUrl)

            } else if (!gltfAssetPath.isNullOrEmpty()) {
                gltfLoader.loadGltfFromAsset(
                    gltfAssetPath,
                    gltfImagePathPrefix,
                    gltfImagePathPostfix
                )

            }
            setUpAnimation()

        }

    }


    private fun setUpLight() {
        coroutineScope.launch {
            if (!lightAssetPath.isNullOrEmpty()) {
                lightManger.setIndirectLightFromAsset(
                    lightAssetPath ?: "",
                    lightIntensity
                )
            } else if (lightIntensity != null) {
                lightManger.setIndirectLight(intensity = lightIntensity)
            } else {
                lightManger.setDefaultLight()
            }
        }
    }

    private fun setUpEnvironment() {
        coroutineScope.launch {
            if (!environmentAssetPath.isNullOrEmpty()) {
                environmentManger.setEnvironmentFromAsset(environmentAssetPath ?: "")
            } else if (environmentColor != null) {
                environmentManger.setEnvironmentFromColor(environmentColor)
            } else {
                environmentManger.setDefaultEnvironment()
                makeSurfaceViewTransparent()
            }
        }
    }


    private fun setUpAnimation() {

        if (autoPlay) {
            if (animationIndex != null) {
                currentAnimationIndex = animationIndex.toInt()
            } else if (!animationName.isNullOrEmpty()) {
                currentAnimationIndex = animationManger.getAnimationIndexByName(animationName)
            }
        } else {
            currentAnimationIndex = null
        }
    }


    private fun makeSurfaceViewTransparent() {
        modelViewer.view.blendMode = View.BlendMode.TRANSLUCENT
        surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)

        val options = modelViewer.renderer.clearOptions
        options.clear = true
        modelViewer.renderer.clearOptions = options


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


    suspend fun changeEnvironment(assetPath: String?): Resource<String> {
        removeFrameCallback()
        val resource = environmentManger.setEnvironmentFromAsset(assetPath)
        if (resource is Resource.Success) {
            makeSurfaceViewNotTransparent()
            environmentAssetPath = assetPath
        }
        addFrameCallback()
        return resource
    }


    fun changeEnvironmentColor(color: Int?): Resource<String> {

        removeFrameCallback()
        val resource = environmentManger.setEnvironmentFromColor(color)
        if(resource is Resource.Success) {
            environmentColor = color
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
            lightAssetPath = assetPath
            lightIntensity = intensity
        }
        addFrameCallback()
        return resource

    }

    fun changeLight(intensity: Double?): Resource<String> {
        return if (intensity != null) {
            removeFrameCallback()
            lightIntensity = intensity
            lightManger.setIndirectLight(intensity)
            addFrameCallback()
            Resource.Success("changed light Intensity successfully")
        } else {
            Resource.Error("light Intensity is invalid")
        }

    }


    fun changeToDefaultLight() {
        removeFrameCallback()
        lightIntensity = LightManger.DEFAULT_LIGHT_INTENSITY
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
        surfaceView.invalidate()
        surfaceView.setZOrderOnTop(true)
        addFrameCallback()

    }

    fun handleOnPause() {
        removeFrameCallback()
    }


    private fun removeFrameCallback() {
        choreographer.removeFrameCallback(frameCallback)

    }

    private fun addFrameCallback() {
        choreographer.postFrameCallback(frameCallback)

    }


    fun destroy() {
        removeFrameCallback()
    }



    private fun Int.getTransform(): Mat4 {
        val tm = modelViewer.engine.transformManager
        return Mat4.of(*tm.getTransform(tm.getInstance(this), null as FloatArray?))
    }

    private fun Int.setTransform(mat: Mat4) {
        val tm = modelViewer.engine.transformManager
        tm.setTransform(tm.getInstance(this), mat.toFloatArray())
    }

    private fun Int.setTransform(mat: FloatArray) {
        val tm = modelViewer.engine.transformManager
        tm.setTransform(tm.getInstance(this), mat)
    }

}

