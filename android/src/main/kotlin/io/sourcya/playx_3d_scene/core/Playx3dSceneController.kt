package io.sourcya.playx_3d_scene.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Choreographer
import android.view.SurfaceView
import androidx.annotation.Size
import com.google.android.filament.Engine
import com.google.android.filament.View
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_3d_scene.core.model.animation.AnimationManger
import io.sourcya.playx_3d_scene.core.model.animation.model.Animation
import io.sourcya.playx_3d_scene.core.model.common.model.Model
import io.sourcya.playx_3d_scene.core.model.common.model.ModelState
import io.sourcya.playx_3d_scene.core.model.glb.loader.GlbLoader
import io.sourcya.playx_3d_scene.core.model.glb.model.GlbModel
import io.sourcya.playx_3d_scene.core.model.gltf.loader.GltfLoader
import io.sourcya.playx_3d_scene.core.model.gltf.model.GltfModel
import io.sourcya.playx_3d_scene.core.scene.camera.CameraManger
import io.sourcya.playx_3d_scene.core.scene.camera.model.Camera
import io.sourcya.playx_3d_scene.core.scene.camera.model.Exposure
import io.sourcya.playx_3d_scene.core.scene.camera.model.LensProjection
import io.sourcya.playx_3d_scene.core.scene.camera.model.Projection
import io.sourcya.playx_3d_scene.core.scene.common.model.Scene
import io.sourcya.playx_3d_scene.core.scene.common.model.SceneState
import io.sourcya.playx_3d_scene.core.scene.common.model.SceneState.Companion.getSceneState
import io.sourcya.playx_3d_scene.core.scene.ground.GroundManger
import io.sourcya.playx_3d_scene.core.scene.ground.model.Ground
import io.sourcya.playx_3d_scene.core.scene.indirect_light.IndirectLightManger
import io.sourcya.playx_3d_scene.core.scene.indirect_light.model.DefaultIndirectLight
import io.sourcya.playx_3d_scene.core.scene.indirect_light.model.HdrIndirectLight
import io.sourcya.playx_3d_scene.core.scene.indirect_light.model.KtxIndirectLight
import io.sourcya.playx_3d_scene.core.scene.light.LightManger
import io.sourcya.playx_3d_scene.core.scene.light.model.Light
import io.sourcya.playx_3d_scene.core.scene.skybox.SkyboxManger
import io.sourcya.playx_3d_scene.core.scene.skybox.model.ColoredSkybox
import io.sourcya.playx_3d_scene.core.scene.skybox.model.HdrSkybox
import io.sourcya.playx_3d_scene.core.scene.skybox.model.KtxSkybox
import io.sourcya.playx_3d_scene.core.shape.ShapeManger
import io.sourcya.playx_3d_scene.core.shape.common.material.MaterialManger
import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape
import io.sourcya.playx_3d_scene.core.shape.common.model.ShapeState
import io.sourcya.playx_3d_scene.core.utils.IBLProfiler
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * This is the main class to handle filament engine.
 * and provide the model viewer for a surface view.
 */
class Playx3dSceneController constructor(
    private val context: Context,
    private var engine: Engine,
    private val iblProfiler: IBLProfiler,
    private val flutterAssets: FlutterAssets,
    private val scene: Scene?,
    private val model: Model?,
    private val shapes: List<Shape>?,

    ) {
    private lateinit var modelViewer: CustomModelViewer
    private val choreographer: Choreographer = Choreographer.getInstance()

    private var modelJob: Job? = null
    private var glbModelStateJob: Job? = null
    private var sceneStateJob: Job? = null
    private var shapeStateJob: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var currentAnimationIndex: Int? = null

    private val surfaceView: SurfaceView = SurfaceView(context)

    private lateinit var glbLoader: GlbLoader

    private lateinit var gltfLoader: GltfLoader

    private lateinit var lightManger: LightManger
    private lateinit var indirectLightManger: IndirectLightManger

    private lateinit var skyboxManger: SkyboxManger

    private lateinit var animationManger: AnimationManger

    private lateinit var cameraManger : CameraManger

    private lateinit var groundManger : GroundManger

    private lateinit var materialManger: MaterialManger

    private lateinit var shapeManger: ShapeManger

    val modelState: MutableStateFlow<ModelState> = MutableStateFlow(ModelState.NONE)
    val sceneState: MutableStateFlow<SceneState> = MutableStateFlow(SceneState.NONE)
    val shapeState: MutableStateFlow<ShapeState> = MutableStateFlow(ShapeState.NONE)


    init {
        setUpViewer()
        setUpGround()
        setUpCamera()
        setUpSkybox()
        setUpLight()
        setUpIndirectLight()
        setUpLoadingModel()
        setUpShapes()
    }



    @SuppressLint("ClickableViewAccessibility")
    private fun setUpViewer() {
        modelViewer = CustomModelViewer(surfaceView, engine)

        surfaceView.setOnTouchListener(modelViewer)
        surfaceView.setZOrderOnTop(true) // necessary


        glbLoader = GlbLoader.getInstance(modelViewer, context, flutterAssets)

        gltfLoader = GltfLoader.getInstance(modelViewer, context, flutterAssets)

        lightManger = LightManger(modelViewer)
        indirectLightManger = IndirectLightManger(modelViewer, iblProfiler, context, flutterAssets)

        skyboxManger = SkyboxManger(modelViewer, iblProfiler, context, flutterAssets)

        animationManger = AnimationManger(modelViewer)
        cameraManger = modelViewer.cameraManger
        materialManger = MaterialManger(modelViewer,context,flutterAssets)
        groundManger = GroundManger(modelViewer,materialManger)

        shapeManger = ShapeManger(modelViewer,materialManger)


    }

    private fun setUpLoadingModel() {
        modelJob = coroutineScope.launch {
            val result = loadModel(model)
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

    private fun setUpCamera(){
        val camera = scene?.camera?: return
        cameraManger.updateCamera(camera)
    }

    private fun setUpGround(){
        coroutineScope.launch {
            groundManger.createGround(scene?.ground)

        }

    }


    private fun setUpShapes() {
        coroutineScope.launch {
            shapeManger.createShapes(shapes)
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


    fun changeSkyboxByColor(color: String?): Resource<String> {

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


    fun changeModelPosition(position: Position?): Resource<String> {
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



    /***===========================================CAMERA===============================================***/

    fun updateCamera(cameraInfo: Camera?): Resource<String> {
        return cameraManger.updateCamera(cameraInfo)

    }
    fun updateExposure(exposure: Exposure?): Resource<String> {
        return cameraManger.updateExposure(exposure)
    }

    fun updateProjection(projection: Projection?): Resource<String> {
        return cameraManger.updateProjection(projection)
    }


    fun updateLensProjection(lensProjection: LensProjection?): Resource<String> {
        return cameraManger.updateLensProjection(lensProjection)
    }


    fun updateCameraShift(shift: DoubleArray?): Resource<String> {
        return cameraManger.updateCameraShift(shift)
    }

    fun updateCameraScaling(scaling: DoubleArray?): Resource<String> {
        return cameraManger.updateCameraScaling(scaling)
    }


    fun setDefaultCamera():Resource<String> {
        cameraManger.setDefaultCamera()
        return Resource.Success("Default camera updated successfully")
    }





    fun lookAtDefaultPosition(): Resource<String> {
        return cameraManger.lookAtDefaultPosition()
    }


    fun lookAtPosition(
        eyeArray: DoubleArray?,
        targetArray: DoubleArray?,
        upwardArray: DoubleArray?,
    ): Resource<String> {
        return cameraManger.lookAtPosition(eyeArray, targetArray, upwardArray)
    }


    fun getLookAt(): Resource<List<Double>> {
        return cameraManger.getLookAt()
    }

    fun scroll(x: Int?, y: Int?, scrollDelta: Float?) :Resource<String>{
        return cameraManger.scroll(x,y,scrollDelta)
    }


    /**
     * Given a viewport coordinate, picks a point in the ground plane.
     */
    @Size(min = 3)
    fun raycast(x: Int?, y: Int?): Resource<FloatArray> {
        return cameraManger.raycast(x, y)
    }

    /**
     * Starts a grabbing session (i.e. the user is dragging around in the viewport).
     *
     * In MAP mode, this starts a panning session.
     * In ORBIT mode, this starts either rotating or strafing.
     * In FREE_FLIGHT mode, this starts a nodal panning session.
     *
     * @param x X-coordinate for point of interest in viewport space
     * @param y Y-coordinate for point of interest in viewport space
     * @param strafe ORBIT mode only; if true, starts a translation rather than a rotation
     */
    fun grabBegin(x: Int?, y: Int?, strafe: Boolean?): Resource<String> {
        return cameraManger.grabBegin(x,y,strafe)
    }

    /**
     * Updates a grabbing session.
     *
     * This must be called at least once between grabBegin / grabEnd to dirty the camera.
     */
    fun grabUpdate(x: Int?, y: Int?): Resource<String> {
        return cameraManger.grabUpdate(x,y)
    }
    fun grabEnd(): Resource<String> {
        return cameraManger.grabEnd()
    }


    suspend fun updateGround(ground: Ground?): Resource<String> {
        return groundManger.updateGround(ground)
    }
    suspend fun updateGroundMaterial(material: Material?): Resource<String> {
        return groundManger.updateGroundMaterial(material)
    }


    suspend fun addShape(shape: Shape?):Resource<String> {
        return shapeManger.addShape(shape)
    }

     fun removeShape(id: Int?):Resource<String> {
        return shapeManger.removeShape(id)
    }

    suspend fun updateShape(id: Int?, shape: Shape?):Resource<String> {
        return shapeManger.updateShape(id,shape)
    }

    fun getCreatedShapesIds(): List<Int> {
        return shapeManger.getCurrentCreatedShapeIds()
    }


    private fun listenToShapeState() {
        shapeStateJob = coroutineScope.launch {
            modelViewer.currentShapesState.collectLatest {
                shapeState.value = it
            }
        }
    }



    private fun listenToModelState() {
        glbModelStateJob = coroutineScope.launch {
            modelViewer.currentModelState.collectLatest {
                modelState.value = it
            }
        }
    }

    private fun listenToSceneState() {
        sceneStateJob = coroutineScope.launch {
            combine(
                modelViewer.currentSkyboxState,
                modelViewer.currentLightState,
                modelViewer.currentGroundState
            ) { (skyboxState, lightState,groundState) ->
                getSceneState(skyboxState, lightState,groundState)
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
        listenToModelState()
        listenToSceneState()
        listenToShapeState()
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
        shapeStateJob?.cancel()
        shapeStateJob = null
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

