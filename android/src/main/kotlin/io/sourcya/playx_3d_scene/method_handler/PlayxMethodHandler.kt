package io.sourcya.playx_3d_scene.method_handler

import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.sourcya.playx_3d_scene.Playx3dScenePlugin
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.controller.ModelViewerController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.util.logging.StreamHandler

/**
 * class to handle method calls from the Flutter side of the plugin.
 */
class PlayxMethodHandler(
    private val messenger: BinaryMessenger,
    private val modelViewer: ModelViewerController?,
    private val id: Int,
) : MethodCallHandler {

    private var job: Job = SupervisorJob()
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + job)

    private var methodChannel: MethodChannel? = null



    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            changeAnimationByIndex -> changeAnimationByIndex(call, result)
            changeAnimationByName -> changeAnimationByName(call, result)
            getAnimationNames -> getAnimationNames(result)
            getAnimationNameByIndex -> getAnimationNameByIndex(call, result)
            getCurrentAnimationIndex -> getCurrentAnimationIndex(result)
            getAnimationCount -> getAnimationCount(result)
            changeEnvironmentByAsset -> changeEnvironmentByAsset(call, result)
            changeEnvironmentColor -> changeEnvironmentColor(call, result)
            changeToTransparentEnvironment -> changeToTransparentEnvironment(result)
            changeLightByAsset -> changeLightByAsset(call, result)
            changeLightByIntensity -> changeLightByIntensity(call, result)
            changeToDefaultLightIntensity -> changeToDefaultLightIntensity(result)
            loadGlbModelFromAssets -> loadGlbModelFromAssets(call, result)
            loadGlbModelFromUrl -> loadGlbModelFromUrl(call, result)
            loadGltfModelFromAssets -> loadGltfModelFromAssets(call, result)
            getCurrentModelState -> getCurrentModelState(result)
            else -> result.notImplemented()
        }
    }

    /**
     *  Changes the current animation by index.
     *      it takes an Int? index as an argument.
     *      and returns the new animation index.
     */
    private fun changeAnimationByIndex(call: MethodCall, result: MethodChannel.Result) {
        val animationIndex = getValue<Int>(call, changeAnimationByIndexKey)
        when (val resource = modelViewer?.changeAnimation(animationIndex)) {
            is Resource.Success -> result.success(resource.data)
            is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
            else -> result.error(
                "Model viewer isn't initialized.",
                "Model viewer isn't initialized.",
                null
            )
        }
    }

    /**
     *  Changes the current animation by animation name.
     *  it takes an String? animation name as an argument.
     *  and returns the new animation index.
     */
    private fun changeAnimationByName(call: MethodCall, result: MethodChannel.Result) {
        val animationName = getValue<String>(call, changeAnimationByNameKey)
        when (val resource = modelViewer?.changeAnimationByName(animationName)) {
            is Resource.Success -> result.success(resource.data)
            is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
            else -> result.error(
                "Model viewer isn't initialized.",
                "Model viewer isn't initialized.",
                null
            )
        }
    }

    /**
     *Get current model animation names.
     */
    private fun getAnimationNames(result: MethodChannel.Result) {
        if (modelViewer != null) {
            result.success(modelViewer.getAnimationNames())
        } else {
            result.error("Model viewer isn't initialized.", "Model viewer isn't initialized.", null)
        }
    }

    /**
     *Get current model animation count.
     */
    private fun getAnimationCount(result: MethodChannel.Result) {
        if (modelViewer != null) {
            result.success(modelViewer.getAnimationCount())
        } else {
            result.error("Model viewer isn't initialized.", "Model viewer isn't initialized.", null)
        }
    }

    /**
     *Get current model animation index.
     */
    private fun getCurrentAnimationIndex(result: MethodChannel.Result) {
        if (modelViewer != null) {
            result.success(modelViewer.getCurrentAnimationIndex())
        } else {
            result.error("Model viewer isn't initialized.", "Model viewer isn't initialized.", null)
        }
    }

    /**
     *Get animation name from index.
     */
    private fun getAnimationNameByIndex(call: MethodCall, result: MethodChannel.Result) {
        val index = getValue<Int>(call, getAnimationNameByIndexKey)
        when (val resource = modelViewer?.getAnimationNameByIndex(index)) {
            is Resource.Success -> result.success(resource.data)
            is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
            else -> result.error(
                "Model viewer isn't initialized.",
                "Model viewer isn't initialized.",
                null
            )

        }
    }

    /**
     *  change environment by given asset path.
     *  it takes an String? asset path as an argument.
     *  should be provided with the KTX skybox file.
     *  so it can update the environment skybox with it.
     */
    private fun changeEnvironmentByAsset(call: MethodCall, result: MethodChannel.Result) {
        coroutineScope.launch {
            val assetPath = getValue<String>(call, changeEnvironmentByAssetKey)
            when (val resource = modelViewer?.changeEnvironment(assetPath)) {
                is Resource.Success -> result.success(resource.data)
                is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
                else -> result.error(
                    "Model viewer isn't initialized.",
                    "Model viewer isn't initialized.",
                    null
                )
            }
        }
    }

    /**  change environment by given color.
     * it takes an Color?  as an argument.
     * and updates the environment skybox color
     */
    private fun changeEnvironmentColor(call: MethodCall, result: MethodChannel.Result) {
        val color: Int? = getValue<Long>(call, changeEnvironmentColorKey)?.toInt()
        when (val resource = modelViewer?.changeEnvironmentColor(color)) {
            is Resource.Success -> result.success(resource.data)
            is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
            else -> result.error(
                "Model viewer isn't initialized.",
                "Model viewer isn't initialized.",
                null
            )
        }
    }

    /**
     * change environment to be transparent
     */
    private fun changeToTransparentEnvironment(result: MethodChannel.Result) {
        if (modelViewer != null) {
            modelViewer.changeToTransparentEnvironment()
            result.success("Environment changed to Transparent")
        } else {
            result.error("Model viewer isn't initialized.", "Model viewer isn't initialized.", null)
        }
    }

    /**
     *change scene indirect light by given asset path.
     * it takes an String? asset path as an argument.
     * and can take light intensity as an argument.
     * should be provided with the KTX image based lighting file.
     * so it can update the scene light with it.
     * if intensity is provide, it will update the scene light intensity with it.
     */
    private fun changeLightByAsset(call: MethodCall, result: MethodChannel.Result) {
        val assetPath: String? = getValue(call, changeLightByAssetKey)
        val intensity: Double? = getValue(call, changeLightByAssetIntensityKey)

        coroutineScope.launch {
            when (val resource = modelViewer?.changeLight(assetPath, intensity)) {
                is Resource.Success -> result.success(resource.data)
                is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
                else -> result.error(
                    "Model viewer isn't initialized.",
                    "Model viewer isn't initialized.",
                    null
                )
            }

        }
    }

    /**
     * change scene indirect light by given intensity.
     * it takes light intensity as an argument.
     * and update the scene light intensity with it.
     */

    private fun changeLightByIntensity(call: MethodCall, result: MethodChannel.Result) {
        val intensity: Double? = getValue(call, changeLightByIntensityKey)

        when (val resource = modelViewer?.changeLight(intensity)) {
            is Resource.Success -> result.success(resource.data)
            is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
            else -> result.error(
                "Model viewer isn't initialized.",
                "Model viewer isn't initialized.",
                null
            )
        }
    }


    /**
     *change scene indirect light to the default intensity which is 40_000.0.
     */
    private fun changeToDefaultLightIntensity(result: MethodChannel.Result) {
        if (modelViewer != null) {
            modelViewer.changeToDefaultLight()
            result.success("Default light intensity changed")
        } else {
            result.error("Model viewer isn't initialized.", "Model viewer isn't initialized.", null)
        }
    }


    /**
     * Load glb model from assets.
     * it takes asset path as an argument.
     * and update the current model with it.

     */
    private fun loadGlbModelFromAssets(call: MethodCall, result: MethodChannel.Result) {
        val assetPath: String? = getValue(call, loadGlbModelFromAssetsPathKey)
        coroutineScope.launch {
            when (val resource = modelViewer?.loadGlbModelFromAssets(assetPath)) {
                is Resource.Success -> result.success(resource.data)
                is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
                else -> result.error(
                    "Model viewer isn't initialized.",
                    "Model viewer isn't initialized.",
                    null
                )
            }
        }
    }

    /**
     *   * Load glb model from url.
     * it takes url as an argument.
     * and update the current model with it.
     */
    private fun loadGlbModelFromUrl(call: MethodCall, result: MethodChannel.Result) {
        val url: String? = getValue(call, loadGlbModelFromUrlKey)
        coroutineScope.launch {
            when (val resource = modelViewer?.loadGlbModelFromUrl(url)) {
                is Resource.Success -> result.success(resource.data)
                is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
                else -> result.error(
                    "Model viewer isn't initialized.",
                    "Model viewer isn't initialized.",
                    null
                )
            }
        }
    }


    /**
     * Load gltf  model from assets.
     * it takes asset path as an argument.
     * and prefix for gltf image assets.
     * if the images path that in the gltf file different from the flutter asset path,
     * you can add prefix to the images path to be before the image.
     * LIKE if in the gltf file, the image path is textures/texture.png
     * and in assets the image path is assets/models/textures/texture.png
     * you will need to add prefix to be 'assets/models/'.
     *and postfix path for gltf image assets.
     * if the images path that in the gltf file different from the flutter asset path,
     * you can add postfix to the images path to be after the image.
     * LIKE if in the gltf file, the image path is assets/textures/texture
     * and in assets the image path is assets/textures/texture.png
     * you will need to add prefix to be '.png'.
     * and update the current model with it.
     */
    private fun loadGltfModelFromAssets(call: MethodCall, result: MethodChannel.Result) {
        val assetPath: String? = getValue(call, loadGltfModelFromAssetsPathKey)
        val prefix: String = getValue(call, loadGltfModelFromAssetsPrefixPathKey) ?: ""
        val postfix: String = getValue(call, loadGltfModelFromAssetsPostfixPathKey) ?: ""
        coroutineScope.launch {
            when (val resource = modelViewer?.loadGltfModelFromAssets(assetPath, prefix, postfix)) {
                is Resource.Success -> result.success(resource.data)
                is Resource.Error -> result.error(resource.message ?: "", resource.message, null)
                else -> result.error(
                    "Model viewer isn't initialized.",
                    "Model viewer isn't initialized.",
                    null
                )
            }
        }
    }


    private fun getCurrentModelState( result: MethodChannel.Result) {
        if (modelViewer != null) {
            result.success(modelViewer.modelState.value.toString())
        } else {
            result.error("Model viewer isn't initialized.", "Model viewer isn't initialized.", null)
        }
    }


    fun startListeningToChannel() {
        methodChannel = MethodChannel(messenger, "${MAIN_CHANNEL_NAME}_$id")
        methodChannel?.setMethodCallHandler(this)
        job = SupervisorJob()

    }

    fun stopListeningToChannel() {
        methodChannel?.setMethodCallHandler(null)
        methodChannel = null
        job.cancel()
    }




    private inline fun <reified T> getValue(call: MethodCall, key: String, default: T? = null): T? {

        if (call.hasArgument(key)) {
            return try {
                call.argument(key) as T?
            } catch (_: Throwable) {
                default
            }
        }
        return default
    }

    companion object {

        private const val MAIN_CHANNEL_NAME = "io.sourcya.playx.3d.scene.channel"

        private  const val changeAnimationByIndex = "CHANGE_ANIMATION_BY_INDEX"
        private const val changeAnimationByIndexKey = "CHANGE_ANIMATION_BY_INDEX_KEY"

        private  const val changeAnimationByName = "CHANGE_ANIMATION_BY_NAME"
        private const val changeAnimationByNameKey = "CHANGE_ANIMATION_BY_NAME_KEY"

        private  const val getAnimationNames = "GET_ANIMATION_NAMES"

        private const val getAnimationNameByIndex = "GET_ANIMATION_NAME_BY_INDEX"
        private    const val getAnimationNameByIndexKey = "GET_ANIMATION_NAME_BY_INDEX_KEY"

        private const val getAnimationCount = "GET_ANIMATION_COUNT"

        private  const val getCurrentAnimationIndex = "GET_CURRENT_ANIMATION_INDEX"

        private  const val changeEnvironmentByAsset = "CHANGE_ENVIRONMENT_BY_ASSET"
        private  const val changeEnvironmentByAssetKey = "CHANGE_ENVIRONMENT_BY_ASSET_KEY"


        private const val changeEnvironmentColor = "CHANGE_ENVIRONMENT_COLOR"
        private  const val changeEnvironmentColorKey = "CHANGE_ENVIRONMENT_COLOR_KEY"

        private  const val changeToTransparentEnvironment = "CHANGE_TO_TRANSPARENT_ENVIRONMENT"

        private const val changeLightByAsset = "CHANGE_LIGHT_BY_ASSET"
        private const val changeLightByAssetKey = "CHANGE_LIGHT_BY_ASSET_KEY"
        private const val changeLightByAssetIntensityKey = "CHANGE_LIGHT_BY_ASSET_INTENSITY_KEY"

        private const val changeLightByIntensity = "CHANGE_LIGHT_BY_INTENSITY"
        private const val changeLightByIntensityKey = "CHANGE_LIGHT_BY_INTENSITY_KEY"

        private const val changeToDefaultLightIntensity = "CHANGE_TO_DEFAULT_LIGHT_INTENSITY"

        private const val loadGlbModelFromAssets = "LOAD_GLB_MODEL_FROM_ASSETS"
        private  const val loadGlbModelFromAssetsPathKey = "LOAD_GLB_MODEL_FROM_ASSETS_PATH_KEY"

        private const val loadGlbModelFromUrl = "LOAD_GLB_MODEL_FROM_URL"
        private const val loadGlbModelFromUrlKey = "LOAD_GLB_MODEL_FROM_URL_KEY"

        private const val loadGltfModelFromAssets = "LOAD_GLTF_MODEL_FROM_ASSETS"
        private const val loadGltfModelFromAssetsPathKey = "LOAD_GLTF_MODEL_FROM_ASSETS_PATH_KEY"
        private const val loadGltfModelFromAssetsPrefixPathKey =
            "LOAD_GLTF_MODEL_FROM_ASSETS_PREFIX_PATH_KEY"
        private const val loadGltfModelFromAssetsPostfixPathKey =
            "LOAD_GLTF_MODEL_FROM_ASSETS_POSTFIX_PATH_KEY"
       private const val getCurrentModelState = "GET_CURRENT_MODEL_STATE";


    }


}