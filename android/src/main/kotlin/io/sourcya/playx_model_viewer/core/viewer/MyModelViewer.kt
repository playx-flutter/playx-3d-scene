package io.sourcya.playx_model_viewer.core.viewer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Choreographer
import android.view.SurfaceView
import com.google.android.filament.View
import com.google.android.filament.utils.Manipulator
import com.google.android.filament.utils.Mat4
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.Utils
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_model_viewer.core.animation.AnimationManger
import io.sourcya.playx_model_viewer.core.environment.EnvironmentManger
import io.sourcya.playx_model_viewer.core.light.LightManger
import io.sourcya.playx_model_viewer.core.loader.GlbLoader
import io.sourcya.playx_model_viewer.core.loader.GltfLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyModelViewer constructor(
    private val context: Context,
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
        loadModel()
        setUpAnimation()


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpViewer() {
        surfaceView = SurfaceView(context)

        choreographer = Choreographer.getInstance()

        modelViewer = ModelViewer(surfaceView)


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

    private fun loadModel() {
        coroutineScope.launch {
            if (!glbAssetPath.isNullOrEmpty()) {
                glbLoader.loadGlbFromAsset(glbAssetPath)
            } else if (!glbUrl.isNullOrEmpty()) {
                glbLoader.loadGlbFromUrl(glbUrl)
            } else if (!gltfAssetPath.isNullOrEmpty()) {
                gltfLoader.loadGltfFromAsset(gltfAssetPath, gltfImagePathPrefix, gltfImagePathPostfix)
            }

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


    fun makeSurfaceViewTransparent() {
        modelViewer.view.blendMode = View.BlendMode.TRANSLUCENT
        surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)

        val options = modelViewer.renderer.clearOptions
        options.clear = true
        modelViewer.renderer.clearOptions = options


    }

    fun makeSurfaceViewNotTransparent() {
        modelViewer.view.blendMode = View.BlendMode.OPAQUE
        surfaceView.setZOrderOnTop(true) // necessary
        surfaceView.holder.setFormat(PixelFormat.OPAQUE)

    }


    fun changeAnimation(animationIndex: Int) {
        currentAnimationIndex = animationIndex
    }

    fun changeAnimation(animationName: String) {
        currentAnimationIndex = animationManger.getAnimationIndexByName(animationName)
    }

    fun getAnimationNames() = animationManger.getAnimationNames()


    fun changeEnvironment(assetPath: String?) {
        removeFrameCallback()
        environmentAssetPath = assetPath
        coroutineScope.launch {
            if (assetPath != null) {
                makeSurfaceViewNotTransparent()
                environmentManger.setEnvironmentFromAsset(assetPath)
            }
        }
        addFrameCallback()
    }


    fun changeEnvironmentColor(color: Int?) {
        removeFrameCallback()
        environmentColor = color
        coroutineScope.launch {
            if (color != null) {
                makeSurfaceViewNotTransparent()
                environmentManger.setEnvironmentFromColor(color)
            }
        }
        addFrameCallback()
    }

    fun changeToTransparentEnvironment() {
        removeFrameCallback()
        environmentManger.setTransparentEnvironment()
        makeSurfaceViewTransparent()
        addFrameCallback()
    }

    fun changeLight(assetPath: String?, intensity: Double? = null) {
        removeFrameCallback()
        lightAssetPath = assetPath
        lightIntensity = intensity
        coroutineScope.launch {
            if (assetPath != null) {
                lightManger.setIndirectLightFromAsset(assetPath, intensity)
            }
            addFrameCallback()
        }
    }

    fun changeLight(intensity: Double?) {
        removeFrameCallback()
        lightIntensity = intensity
        coroutineScope.launch {
            if (intensity != null) {
                lightManger.setIndirectLight(intensity)
            }
            addFrameCallback()
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
                modelViewer.render(currentTime)
            } catch (_: Exception) {
            }
        }
    }


    fun handleOnResume() {
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

    }


    companion object {

        init {
            Utils.init()
        }


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

