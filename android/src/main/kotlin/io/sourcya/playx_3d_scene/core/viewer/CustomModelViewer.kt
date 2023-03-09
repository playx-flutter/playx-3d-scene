package io.sourcya.playx_3d_scene.core.viewer

import android.hardware.lights.LightState
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceView
import android.view.TextureView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.*
import com.google.android.filament.utils.*
import io.sourcya.playx_3d_scene.core.light.LightManger
import io.sourcya.playx_3d_scene.core.loader.ModelLoader
import io.sourcya.playx_3d_scene.core.models.states.ModelState
import io.sourcya.playx_3d_scene.core.models.states.SceneState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
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

    var cameraFocalLength = 28f
        set(value) {
            field = value
            updateCameraProjection()
        }

    val scene: Scene = engine.createScene()
    val view: View = engine.createView()
    private val camera: Camera = engine.createCamera(engine.entityManager.create())
        .apply { setExposure(kAperture, kShutterSpeed, kSensitivity) }
    val renderer: Renderer = engine.createRenderer()
    val modelLoader: ModelLoader = ModelLoader(this)

    val currentModelState = MutableStateFlow(ModelState.NONE)
    val rendererStateFlow:MutableStateFlow<Long?> = MutableStateFlow(null)
    val currentSkyboxState = MutableStateFlow(SceneState.NONE)
    val currentLightState = MutableStateFlow(SceneState.NONE)



    private lateinit var displayHelper: DisplayHelper
    private lateinit var cameraManipulator: Manipulator
    private lateinit var gestureDetector: GestureDetector
    private var surfaceView: SurfaceView? = null
    private var textureView: TextureView? = null


    private var swapChain: SwapChain? = null

    private val eyePos = DoubleArray(3)
    private val target = DoubleArray(3)
    private val upward = DoubleArray(3)

    init {
        view.scene = scene
        view.camera = camera


    }

    constructor(
        surfaceView: SurfaceView,
        engine: Engine = Engine.create(),
        uiHelper: UiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK),
        manipulator: Manipulator? = null
    ) : this(engine, uiHelper) {
        cameraManipulator = manipulator ?: Manipulator.Builder()
            .targetPosition(
                kDefaultObjectPosition.x,
                kDefaultObjectPosition.y,
                kDefaultObjectPosition.z
            )
            .viewport(surfaceView.width, surfaceView.height)
            .build(Manipulator.Mode.ORBIT)

        this.surfaceView = surfaceView
        gestureDetector = GestureDetector(surfaceView, cameraManipulator)
        displayHelper = DisplayHelper(surfaceView.context)
        uiHelper.renderCallback = SurfaceCallback()
        uiHelper.attachTo(surfaceView)
    }

    @Suppress("unused")
    constructor(
        textureView: TextureView,
        engine: Engine = Engine.create(),
        uiHelper: UiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK),
        manipulator: Manipulator? = null
    ) : this(engine, uiHelper) {
        cameraManipulator = manipulator ?: Manipulator.Builder()
            .targetPosition(
                kDefaultObjectPosition.x,
                kDefaultObjectPosition.y,
                kDefaultObjectPosition.z
            )
            .viewport(textureView.width, textureView.height)
            .build(Manipulator.Mode.ORBIT)

        this.textureView = textureView
        gestureDetector = GestureDetector(textureView, cameraManipulator)
        displayHelper = DisplayHelper(textureView.context)
        uiHelper.renderCallback = SurfaceCallback()

        uiHelper.attachTo(textureView)
    }


    /**
     * Sets up a root transform on the current model to make it fit into a unit cube.
     *
     * @param centerPoint Coordinate of center point of unit cube, defaults to < 0, 0, -4 >
     */
    fun transformToUnitCube(centerPoint: Float3 = kDefaultObjectPosition, scale: Float = 1.0f) {
        modelLoader.transformToUnitCube(centerPoint,scale)
    }

    /**
     * Removes the transformation that was set up via transformToUnitCube.
     */
    fun clearRootTransform() {
        modelLoader.clearRootTransform()
    }


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

        // Extract the camera basis from the helper and push it to the Filament camera.
        cameraManipulator.getLookAt(eyePos, target, upward)
        camera.lookAt(
            eyePos[0], eyePos[1], eyePos[2],
            target[0], target[1], target[2],
            upward[0], upward[1], upward[2]
        )

        // Render the scene, unless the renderer wants to skip the frame.
        if (renderer.beginFrame(swapChain!!, frameTimeNanos)) {
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
        modelLoader.destroy()
        engine.destroyRenderer(renderer)
        engine.destroyView(this@CustomModelViewer.view)
        engine.destroyScene(scene)
        engine.destroyCameraComponent(camera.entity)
        EntityManager.get().destroy(camera.entity)
    }

    /**
     * Handles a [MotionEvent] to enable one-finger orbit, two-finger pan, and pinch-to-zoom.
     */
    fun onTouchEvent(event: MotionEvent) {
        gestureDetector.onTouchEvent(event)
    }

    @SuppressWarnings("ClickableViewAccessibility")
    override fun onTouch(view: android.view.View, event: MotionEvent): Boolean {
        onTouchEvent(event)
        return true
    }


    private fun updateCameraProjection() {
        val width = view.viewport.width
        val height = view.viewport.height
        val aspect = width.toDouble() / height.toDouble()
        camera.setLensProjection(cameraFocalLength.toDouble(), aspect, kNearPlane, kFarPlane)
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
            cameraManipulator.setViewport(width, height)
            updateCameraProjection()
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
    companion object {
        val kDefaultObjectPosition = Float3(0.0f, 0.0f, -4.0f)
        private const val kNearPlane = 0.05     // 5 cm
        private const val kFarPlane = 1000.0    // 1 km
        private const val kAperture = 16f
        private const val kShutterSpeed = 1f / 125f
        private const val kSensitivity = 100f
    }
}