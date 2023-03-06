package io.sourcya.playx_3d_scene.view

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.filament.Engine
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.platform.PlatformView
import io.sourcya.playx_3d_scene.method_handler.PlayxMethodHandler
import io.sourcya.playx_3d_scene.core.controller.ModelViewerController
import io.sourcya.playx_3d_scene.core.models.model.Model
import io.sourcya.playx_3d_scene.core.models.scene.Scene
import io.sourcya.playx_3d_scene.utils.LifecycleProvider
import io.sourcya.playx_3d_scene.utils.getMapValue
import timber.log.Timber


class Playx3dScene(
    private val context: Context,
    private val id: Int,
    private val creationParams: Map<String?, Any?>?,
    private val binding: FlutterPlugin.FlutterPluginBinding,
    private val engine: Engine,
    private val lifecycleProvider: LifecycleProvider
) : PlatformView, LifecycleEventObserver {
    private var modelViewer: ModelViewerController? = null
    private var playXMethodHandler: PlayxMethodHandler? = null

    init {
        setUpModelViewer()
    }


    private fun setUpModelViewer() {

        val modelMap = getMapValue<Map<String?, Any?>>("model",creationParams)
        val sceneMap = getMapValue<Map<String?, Any?>>("scene",creationParams)

        val model = Model.fromMap(modelMap)
        val scene = Scene.fromMap(sceneMap)

        modelViewer = ModelViewerController(
            context,
            engine,
            binding.flutterAssets,
            model = model,
            scene = scene
        )


    }

    private fun listenToChannel() {
        playXMethodHandler = PlayxMethodHandler(binding.binaryMessenger, modelViewer, id)
        playXMethodHandler?.startListeningToChannel()
        lifecycleProvider.getLifecycle()?.addObserver(this)
    }

    private fun stopListeningToChannel() {
        playXMethodHandler?.stopListeningToChannel()
        playXMethodHandler = null
        lifecycleProvider.getLifecycle()?.removeObserver(this)

    }


    override fun onFlutterViewAttached(flutterView: View) {
        listenToChannel()
        modelViewer?.handleOnResume()

        super.onFlutterViewAttached(flutterView)
    }


    override fun onFlutterViewDetached() {
        stopListeningToChannel()
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

        } else if (event == Lifecycle.Event.ON_PAUSE) {
            modelViewer?.handleOnPause()
        }

    }
}