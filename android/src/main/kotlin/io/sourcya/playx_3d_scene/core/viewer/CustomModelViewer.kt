package io.sourcya.playx_3d_scene.core.viewer

import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceView
import android.view.TextureView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.Animator
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.utils.Float3
import io.sourcya.playx_3d_scene.core.model.common.loader.ModelLoader
import io.sourcya.playx_3d_scene.core.model.common.model.ModelState
import io.sourcya.playx_3d_scene.core.scene.camera.CameraManger
import io.sourcya.playx_3d_scene.core.scene.common.model.SceneState
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.ShapeState
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.nio.Buffer



/**
 * Helps render glTF models into a [SurfaceView] or [TextureView] with an orbit controller.
 *
 * `ModelViewer` owns a Filament engine, renderer, swapchain, view, and scene. It allows clients
 * to access these objects via read-only properties. The viewer can display only one glTF scene
 * at a time, which can be scaled and translated into the viewing frustum by calling
 * [transformToUnitCube]. All ECS entities can be accessed and modified via the [asset] property.
 *
 * For GLB files, clients can call [loadModelGlb] and pass in a [Buffer] with the contents of the
 * GLB file. For glTF files, clients can call [loadModelGltf] and pass in a [Buffer] with the JSON
 * contents, as well as a callback for loading external resources.
 *
 * `ModelViewer` reduces much of the boilerplate required for simple Filament applications, but
 * clients still have the responsibility of adding an [IndirectLight] and [Skybox] to the scene.
 * Additionally, clients should:
 *
 * 1. Pass the model viewer into [SurfaceView.setOnTouchListener] or call its [onTouchEvent]
 *    method from your touch handler.
 * 2. Call [render] and [Animator.applyAnimation] from a `Choreographer` frame callback.
 *
 * NOTE: if its associated SurfaceView or TextureView has become detached from its window, the
 * ModelViewer becomes invalid and must be recreated.
 *
 * See `sample-gltf-viewer` for a usage example.
 */
class CustomModelViewer(
    val engine: Engine,
    private val uiHelper: UiHelper
) : android.view.View.OnTouchListener {

    var animator: Animator? = null


    val scene: Scene = engine.createScene()
    val view: View = engine.createView()
    val renderer: Renderer = engine.createRenderer().apply {
        clearOptions.clearColor = floatArrayOf(1f,1f,1f,1f)

    }

    val currentModelState = MutableStateFlow(ModelState.NONE)
    val rendererStateFlow:MutableStateFlow<Long?> = MutableStateFlow(null)
    val currentSkyboxState = MutableStateFlow(SceneState.NONE)
    val currentLightState = MutableStateFlow(SceneState.NONE)
    val currentGroundState = MutableStateFlow(SceneState.NONE)

    val currentShapesState = MutableStateFlow(ShapeState.NONE)

    lateinit var  cameraManger : CameraManger



    private lateinit var displayHelper: DisplayHelper
    private var surfaceView: SurfaceView? = null
    private var textureView: TextureView? = null
    private lateinit var assetLoader: AssetLoader
    private lateinit var resourceLoader: ResourceLoader
    lateinit var modelLoader: ModelLoader;

    private var swapChain: SwapChain? = null


    init {
        view.scene = scene


    }

    constructor(
        surfaceView: SurfaceView,
        engine: Engine = Engine.create(),
        assetLoader: AssetLoader,
        resourceLoader: ResourceLoader,
        uiHelper: UiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK),
    ) : this(engine, uiHelper) {

        this.surfaceView = surfaceView
        cameraManger = CameraManger(this,view,surfaceView)
        view.camera = cameraManger.camera


        displayHelper = DisplayHelper(surfaceView.context)
        uiHelper.renderCallback = SurfaceCallback()
        uiHelper.attachTo(surfaceView)

        this.assetLoader = assetLoader;
        this.resourceLoader  = resourceLoader

        setupView()
        modelLoader = ModelLoader(this, assetLoader, resourceLoader)

    }

    @Suppress("unused")
    constructor(
        textureView: TextureView,
        engine: Engine = Engine.create(),
        assetLoader: AssetLoader,
        resourceLoader: ResourceLoader,
        uiHelper: UiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK),
    ) : this(engine, uiHelper) {


        this.textureView = textureView
        cameraManger = CameraManger(this,view,textureView)
        view.camera = cameraManger.camera
        displayHelper = DisplayHelper(textureView.context)
        uiHelper.renderCallback = SurfaceCallback()

        uiHelper.attachTo(textureView)
        this.assetLoader = assetLoader;
        this.resourceLoader  = resourceLoader

        setupView()

        modelLoader = ModelLoader(this, assetLoader, resourceLoader)

    }


    /**
     * Sets up a root transform on the current model to make it fit into a unit cube.
     *
     * @param centerPoint Coordinate of center point of unit cube, defaults to < 0, 0, -4 >
     */
    fun transformToUnitCube(centerPoint: Position?, scale: Float? ) {
        modelLoader.transformToUnitCube(centerPoint,scale)
    }

    /**
     * Removes the transformation that was set up via transformToUnitCube.
     */
    fun clearRootTransform() {
        modelLoader.clearRootTransform()
    }


    private fun setupView(){

            view.let {

            //on mobile, better use lower quality color buffer
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
    }

    fun getModelTransform()= modelLoader.getModelTransform()

    /**
     * Renders the model and updates the Filament camera.
     *
     * @param frameTimeNanos time in nanoseconds when the frame started being rendered,
     *                       typically comes from {@link android.view.Choreographer.FrameCallback}
     */

    fun render(frameTimeNanos: Long) {
        if (!uiHelper.isReadyToRender) {
            return
        }


        modelLoader.updateScene()

        cameraManger.lookAtDefaultPosition()

        val beginFrame = renderer.beginFrame(swapChain!!, frameTimeNanos)
        // Render the scene, unless the renderer wants to skip the frame.
        if (beginFrame) {
            renderer.render(view)
            renderer.endFrame()
            rendererStateFlow.value=frameTimeNanos
        }
    }

    fun destroyModel() {
        modelLoader.destroyModel()
    }

    fun destroySkybox(){
        scene.skybox?.let { engine.destroySkybox(it) }
    }

    fun destroyIndirectLight( ){
        scene.indirectLight?.let { engine.destroyIndirectLight(it) }
    }
    fun destroy() {
        uiHelper.detach()
        modelLoader.destroyModel()
        engine.destroyRenderer(renderer)
        engine.destroyView(this@CustomModelViewer.view)
        engine.destroyScene(scene)
        cameraManger.destroyCamera()
    }

    /**
     * Handles a [MotionEvent] to enable one-finger orbit, two-finger pan, and pinch-to-zoom.
     */
    private fun onTouchEvent(event: MotionEvent) {
        cameraManger.onTouchEvent(event)
    }

    @SuppressWarnings("ClickableViewAccessibility")
    override fun onTouch(view: android.view.View, event: MotionEvent): Boolean {
        onTouchEvent(event)
        return true
    }


    inner class SurfaceCallback : UiHelper.RendererCallback {
        override fun onNativeWindowChanged(surface: Surface) {
            swapChain?.let { engine.destroySwapChain(it) }
            swapChain = engine.createSwapChain(surface)


            surfaceView?.let { displayHelper.attach(renderer, it.display) }
            textureView?.let { displayHelper.attach(renderer, it.display) }
        }

        override fun onDetachedFromSurface() {
            displayHelper.detach()
            swapChain?.let {
                engine.destroySwapChain(it)
                engine.flushAndWait()
                swapChain = null
        }
        }

        override fun onResized(width: Int, height: Int) {
            view.viewport = Viewport(0, 0, width, height)
            cameraManger.updateCameraOnResize(width,height)
        }
    }


    fun setModelState(state : ModelState){
        currentModelState.value = state
    }

    fun setLightState(state : SceneState){
        currentLightState.value = state
    }

    fun setSkyboxState(state : SceneState){
        currentSkyboxState.value = state
    }
    fun setGroundState(state : SceneState){
        currentGroundState.value = state
    }
    companion object {
        val kDefaultObjectPosition = Float3(0.0f, 0.0f, -4.0f)
    }
}