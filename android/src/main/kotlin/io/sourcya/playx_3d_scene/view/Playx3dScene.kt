package io.sourcya.playx_3d_scene.view

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.filament.Engine
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.ResourceLoader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.platform.PlatformView
import io.sourcya.playx_3d_scene.core.Playx3dSceneController
import io.sourcya.playx_3d_scene.core.model.common.model.Model
import io.sourcya.playx_3d_scene.core.scene.common.model.Scene
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape
import io.sourcya.playx_3d_scene.core.utils.IBLProfiler
import io.sourcya.playx_3d_scene.method_handler.PlayxEventHandler
import io.sourcya.playx_3d_scene.method_handler.PlayxMethodHandler
import io.sourcya.playx_3d_scene.utils.LifecycleProvider
import io.sourcya.playx_3d_scene.utils.getMapValue
import timber.log.Timber


class Playx3dScene(
    private val context: Context,
    private val id: Int,
    private val creationParams: Map<String?, Any?>?,
    private val binding: FlutterPlugin.FlutterPluginBinding,
    private val engine: Engine,
    private val iblProfiler: IBLProfiler,
    private val assetLoader: AssetLoader,
    private val resourceLoader: ResourceLoader,
    private val lifecycleProvider: LifecycleProvider
) : PlatformView, LifecycleEventObserver {
    private var modelViewer: Playx3dSceneController? = null
    private var playXMethodHandler: PlayxMethodHandler? = null
    private var playxEventHandler: PlayxEventHandler? = null

    init {
        setUpModelViewer()
        listenToChannel()

    }


    private fun setUpModelViewer() {
        val modelMap = getMapValue<Map<String?, Any?>>("model", creationParams)
        val sceneMap = getMapValue<Map<String?, Any?>>("scene", creationParams)
        val shapeList = getMapValue<List<Any>>("shapes",creationParams)

        val model = Model.fromMap(modelMap)
        val scene = Scene.fromMap(sceneMap)
        val shapes = Shape.fromJson(shapeList)

        modelViewer = Playx3dSceneController(
            context,
            engine,
            iblProfiler,
            assetLoader,
            resourceLoader,
            binding.flutterAssets,
            model = model,
            scene = scene,
            shapes = shapes,
            id = id,
        )


    }

    private fun listenToChannel() {
        playXMethodHandler = PlayxMethodHandler(binding.binaryMessenger, modelViewer, id)
        playXMethodHandler?.startListeningToChannel()
        playxEventHandler = PlayxEventHandler(binding.binaryMessenger, modelViewer, id)
        playxEventHandler?.startListeningToEventChannels()
        lifecycleProvider.getLifecycle()?.addObserver(this)
    }

    private fun stopListeningToChannel() {
        playXMethodHandler?.stopListeningToChannel()
        playxEventHandler?.stopListeningToEventChannels()
        playXMethodHandler = null
        playxEventHandler = null
        lifecycleProvider.getLifecycle()?.removeObserver(this)

    }


    override fun onFlutterViewAttached(flutterView: View) {
        modelViewer?.handleOnResume()

        super.onFlutterViewAttached(flutterView)
    }


    override fun onFlutterViewDetached() {
        modelViewer?.handleOnPause()
        super.onFlutterViewDetached()

    }

    override fun getView(): View? {
        return modelViewer?.getView()
    }

    override fun dispose() {
        modelViewer?.destroy()
        stopListeningToChannel()
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            modelViewer?.handleOnResume()
            playxEventHandler?.handleOnResume()

        } else if (event == Lifecycle.Event.ON_PAUSE) {
            playxEventHandler?.handleOnPause()
            modelViewer?.handleOnPause()

        }

    }
}