package io.sourcya.playx_model_viewer.view

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.renderer.FlutterRenderer
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import io.sourcya.playx_model_viewer.PlayXMethodHandler
import io.sourcya.playx_model_viewer.PlayxModelViewerPlugin.Companion.channelName
import io.sourcya.playx_model_viewer.core.viewer.MyModelViewer
import timber.log.Timber


class PlayXModelViewer(
    private val context: Context,
    private val id: Int,
    private val creationParams: Map<String?, Any?>?,
    private val binding: FlutterPlugin.FlutterPluginBinding
) : PlatformView {

    private lateinit var modelViewer: MyModelViewer
    private lateinit var methodChannel: MethodChannel

    init {
        setUpModelViewer()
        setUpChannel()
    }


    private fun setUpModelViewer() {
        Timber.d("PLAYX MODEL VIEWER 1: #setUpModelViewer $creationParams")

        modelViewer = MyModelViewer(
            context,
            binding.flutterAssets,
            glbAssetPath = getValue(glbAssetPathKey),
            glbUrl = getValue(glbUrlKey),
            gltfAssetPath = getValue(gltfAssetPathKey),
            gltfImagePathPrefix = getValue(gltfImagePathPrefixKey)?:"",
            gltfImagePathPostfix = getValue(gltfImagePathPostfixKey) ?:"",
            lightAssetPath = getValue(lightAssetPathKey),
            lightIntensity = getValue(lightIntensityKey),
            environmentAssetPath = getValue(environmentAssetPathKey),
            environmentColor = getValue<Long>(environmentColorKey)?.toInt(),
            animationIndex = getValue(animationIndexKey),
            animationName = getValue(animationNameKey),
            autoPlay = getValue(autoPlayKey) ?: false,
        )


    }

    private fun setUpChannel() {
        Timber.d("PLAYX MODEL VIEWER : #setUpChannel")

        methodChannel = MethodChannel(binding.binaryMessenger, "${channelName}_$id")
        methodChannel.setMethodCallHandler(PlayXMethodHandler(binding.flutterAssets))

    }

    override fun onFlutterViewAttached(flutterView: View) {
        super.onFlutterViewAttached(flutterView)
        modelViewer.handleOnResume()
    }

    override fun onFlutterViewDetached() {
        super.onFlutterViewDetached()
        modelViewer.handleOnPause()
    }

    override fun getView(): View {
        Timber.d("PLAYX MODEL VIEWER : #getView")

        return modelViewer.surfaceView
    }

    override fun dispose() {
        modelViewer.destroy()
    }


    private inline fun <reified T> getValue(key: String, default: T? = null): T? {

        val item = creationParams?.get(key)
        Timber.d(
            "MY PLAYX MODEL VIEWER 1: getValue " +
                    ":$key : item :$item "
        )

        if (item != null) {


            try {
                Timber.d(
                    "MY PLAYX MODEL VIEWER 4: getValue :$key :" +
                            " item :${item as T?}"
                )

            } catch (e: Throwable) {
                Timber.d(
                    "MY PLAYX MODEL VIEWER 5: getValue :$key :" +
                            " message :${e.message}"
                )

            }

            if (item is T) {
                Timber.d(
                    "MY PLAYX MODEL VIEWER 2: getValue :$key :" +
                            " item :$item value $item"
                )
                return item
            }
            Timber.d(
                "MY PLAYX MODEL VIEWER 3: getValue :$key :" +
                        " item :$item value $item"
            )

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
}