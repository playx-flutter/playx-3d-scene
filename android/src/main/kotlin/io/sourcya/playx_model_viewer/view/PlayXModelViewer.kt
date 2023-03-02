package io.sourcya.playx_model_viewer.view

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.filament.Engine
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.platform.PlatformView
import io.sourcya.playx_model_viewer.method_handler.PlayXMethodHandler
import io.sourcya.playx_model_viewer.core.controller.ModelViewerController
import io.sourcya.playx_model_viewer.utils.LifecycleProvider


class PlayXModelViewer(
    private val context: Context,
    private val id: Int,
    private val creationParams: Map<String?, Any?>?,
    private val binding: FlutterPlugin.FlutterPluginBinding,
    private val engine: Engine,
    private val lifecycleProvider: LifecycleProvider
) : PlatformView, LifecycleEventObserver {

    private var modelViewer: ModelViewerController? = null
    private var playXMethodHandler: PlayXMethodHandler? = null

    init {
        setUpModelViewer()
    }


    private fun setUpModelViewer() {
        modelViewer = ModelViewerController(
            context,
            engine,
            binding.flutterAssets,
            glbAssetPath = getValue(glbAssetPathKey),
            glbUrl = getValue(glbUrlKey),
            gltfAssetPath = getValue(gltfAssetPathKey),
            gltfImagePathPrefix = getValue(gltfImagePathPrefixKey) ?: "",
            gltfImagePathPostfix = getValue(gltfImagePathPostfixKey) ?: "",
            lightAssetPath = getValue(lightAssetPathKey),
            lightIntensity = getValue(lightIntensityKey),
            environmentAssetPath = getValue(environmentAssetPathKey),
            environmentColor = getValue<Long>(environmentColorKey)?.toInt(),
            animationIndex = getValue(animationIndexKey),
            animationName = getValue(animationNameKey),
            autoPlay = getValue(autoPlayKey) ?: false,
        )


    }

    private fun listenToChannel() {
        playXMethodHandler = PlayXMethodHandler(binding.binaryMessenger, modelViewer, id)
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

    private inline fun <reified T> getValue(key: String, default: T? = null): T? {
        val item = creationParams?.get(key)

        if (item is T) {
            return item
        }
        return default
    }

    companion object {


        const val glbAssetPathKey = "GLB_ASSET_PATH_KEY"
        const val glbUrlKey = "GLB_URL_KEY"
        const val gltfAssetPathKey = "GLTF_ASSET_PATH_KEY"
        const val gltfImagePathPrefixKey = "GLTF_IMAGE_PATH_PREFIX_KEY"
        const val gltfImagePathPostfixKey = "GLTF_IMAGE_PATH_POSTFIX_KEY"
        const val lightAssetPathKey = "LIGHT_ASSET_PATH_KEY"
        const val lightIntensityKey = "LIGHT_INTENSITY_KEY"
        const val environmentAssetPathKey = "ENVIRONMENT_ASSET_PATH_KEY"
        const val environmentColorKey = "ENVIRONMENT_COLOR_KEY"
        const val animationIndexKey = "ANIMATION_INDEX_KEY"
        const val animationNameKey = "ANIMATION_NAME_KEY"
        const val autoPlayKey = "AUTO_PLAY_KEY"


    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
             modelViewer?.handleOnResume()

        }
        else if (event == Lifecycle.Event.ON_PAUSE){
            modelViewer?.handleOnPause()
        }

    }
}