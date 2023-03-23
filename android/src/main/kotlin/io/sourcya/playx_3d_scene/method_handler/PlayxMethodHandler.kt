package io.sourcya.playx_3d_scene.method_handler

import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.sourcya.playx_3d_scene.core.Playx3dSceneController
import io.sourcya.playx_3d_scene.core.scene.camera.model.Camera
import io.sourcya.playx_3d_scene.core.scene.camera.model.Exposure
import io.sourcya.playx_3d_scene.core.scene.camera.model.LensProjection
import io.sourcya.playx_3d_scene.core.scene.camera.model.Projection
import io.sourcya.playx_3d_scene.core.scene.ground.model.Ground
import io.sourcya.playx_3d_scene.core.scene.indirect_light.model.DefaultIndirectLight
import io.sourcya.playx_3d_scene.core.scene.light.model.Light
import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.utils.toObject
import kotlinx.coroutines.*

/**
 * class to handle method calls from the Flutter side of the plugin.
 */
class PlayxMethodHandler(
    private val messenger: BinaryMessenger,
    private val modelViewer: Playx3dSceneController?,
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
            changeSkyboxByAsset -> changeSkyboxByKtxAsset(call, result)
            changeSkyboxByUrl->changeSkyboxByKtxUrl(call,result)
            changeSkyboxByHdrAsset-> changeSkyboxByHdrAsset(call,result)
            changeSkyboxByHdrUrl-> changeSkyboxByHdrUrl(call, result)
            changeSkyboxColor -> changeSkyboxColor(call, result)
            changeToTransparentSkybox -> changeToTransparentSkybox(result)
            changeIndirectLightByKtxAsset -> changeIndirectLightByKtxAsset(call, result)
            changeIndirectLightByKtxUrl ->changeIndirectLightByKtxUrl(call,result)
            changeIndirectLightByHdrAsset -> changeIndirectLightByHdrAsset(call, result)
            changeIndirectLightByHdrUrl -> changeIndirectLightByHdrUrl(call, result)
            changeIndirectLightByDefaultIndirectLight -> changeIndirectLightByDefaultIndirectLight(call, result)
            changeToDefaultIndirectLight -> changeToDefaultIndirectLight(result)
            changeLight -> changeSceneLight(call, result)
            changeToDefaultLight -> changeToDefaultLight( result)
            loadGlbModelFromAssets -> loadGlbModelFromAssets(call, result)
            loadGlbModelFromUrl -> loadGlbModelFromUrl(call, result)
            loadGltfModelFromAssets -> loadGltfModelFromAssets(call, result)
            changeModelScale -> changeModelScale(call, result)
            changeModelPosition -> changeModelPosition(call, result)
            getCurrentModelState -> getCurrentModelState(result)
            updateCamera ->updateCamera(call,result)
            updateExposure->updateExposure(call,result)
            updateProjection -> updateProjection(call,result)
            updateLensProjection -> updateLensProjection(call,result)
            updateCameraShift->updateCameraShift(call,result)
            updateCameraScaling->updateCameraScaling(call,result)
            setDefaultCamera -> setDefaultCamera(result)
            lookAtDefaultPosition -> lookAtDefaultPosition(result)
            lookAtPosition -> lookAtCameraPosition(call,result)
            getLookAt -> getCameraLookAtPositions(result)
            cameraScroll -> scrollCameraTo(call,result)
            cameraGrabBegin-> beginCameraGrab(call,result)
            cameraGrabUpdate -> updateCameraGrab(call,result)
            cameraGrabEnd-> endCameraGrab(result)
            cameraRayCast-> getCameraRayCast(call,result)
            updateGround -> updateGround(call,result)
            updateGroundMaterial -> updateGroundMaterial(call,result)
            addShape->addShape(call,result)
            removeShape -> removeShape(call,result)
            updateShape -> updateShape(call,result)
            getCurrentCreatedShapesIds -> getCurrentCreatedShapesIds(result)
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
    private fun changeSkyboxByKtxAsset(call: MethodCall, result: MethodChannel.Result) {
        coroutineScope.launch {
            val assetPath = getValue<String>(call, changeSkyboxByAssetKey)
            when (val resource = modelViewer?.changeSkyboxFromKtxAsset(assetPath)) {
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
     *  change environment by given asset path.
     *  it takes an String? asset path as an argument.
     *  should be provided with the KTX skybox file.
     *  so it can update the environment skybox with it.
     */
    private fun changeSkyboxByKtxUrl(call: MethodCall, result: MethodChannel.Result) {
        coroutineScope.launch {
            val url = getValue<String>(call, changeSkyboxByUrlKey)
            when (val resource = modelViewer?.changeSkyboxFromKtxUrl(url)) {
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
     *  change environment by given asset path.
     *  it takes an String? asset path as an argument.
     *  should be provided with the KTX skybox file.
     *  so it can update the environment skybox with it.
     */
    private fun changeSkyboxByHdrAsset(call: MethodCall, result: MethodChannel.Result) {
        coroutineScope.launch {
            val assetPath = getValue<String>(call, changeSkyboxByHdrAssetKey)
            when (val resource = modelViewer?.changeSkyboxFromHdrAsset(assetPath)) {
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
     *  change environment by given asset path.
     *  it takes an String? asset path as an argument.
     *  should be provided with the KTX skybox file.
     *  so it can update the environment skybox with it.
     */
    private fun changeSkyboxByHdrUrl(call: MethodCall, result: MethodChannel.Result) {
        coroutineScope.launch {
            val url = getValue<String>(call, changeSkyboxByHdrUrlKey)
            when (val resource = modelViewer?.changeSkyboxFromHdrUrl(url)) {
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
    private fun changeSkyboxColor(call: MethodCall, result: MethodChannel.Result) {
        val color: String? = getValue(call, changeSkyboxColorKey)
        when (val resource = modelViewer?.changeSkyboxByColor(color)) {
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
    private fun changeToTransparentSkybox(result: MethodChannel.Result) {
        if (modelViewer != null) {
            modelViewer.changeToTransparentSkybox()
            result.success("Skybox changed to Transparent")
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
    private fun changeIndirectLightByKtxAsset(call: MethodCall, result: MethodChannel.Result) {
        val assetPath: String? = getValue(call, changeIndirectLightByKtxAssetKey)
        val intensity: Double? = getValue(call, changeIndirectLightByKtxAssetIntensityKey)


        coroutineScope.launch {
            when (val resource = modelViewer?.changeIndirectLightFromKtxAsset(assetPath, intensity)) {
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
     *change scene indirect light by given ktx url.
     * it takes an String? asset path as an argument.
     * and can take light intensity as an argument.
     * should be provided with the KTX image based lighting file.
     * so it can update the scene light with it.
     * if intensity is provide, it will update the scene light intensity with it.
     */
    private fun changeIndirectLightByKtxUrl(call: MethodCall, result: MethodChannel.Result) {
        val url: String? = getValue(call, changeIndirectLightByKtxUrlKey)
        val intensity: Double? = getValue(call, changeIndirectLightByKtxUrlIntensityKey)

        coroutineScope.launch {
            when (val resource = modelViewer?.changeIndirectLightFromKtxUrl(url, intensity)) {
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
     *change scene indirect light by given asset path.
     * it takes an String? asset path as an argument.
     * and can take light intensity as an argument.
     * should be provided with the HDR  file.
     * so it can update the scene light with it.
     * if intensity is provide, it will update the scene light intensity with it.
     */
    private fun changeIndirectLightByHdrAsset(call: MethodCall, result: MethodChannel.Result) {
        val assetPath: String? = getValue(call, changeIndirectLightByHdrAssetKey)
        val intensity: Double? = getValue(call, changeIndirectLightByHdrAssetIntensityKey)

        coroutineScope.launch {
            when (val resource = modelViewer?.changeIndirectLightFromHdrAsset(assetPath, intensity)) {
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
     *change scene indirect light by given Hdr url.
     * it takes an String? asset path as an argument.
     * and can take light intensity as an argument.
     * should be provided with the HDR file.
     * so it can update the scene light with it.
     * if intensity is provide, it will update the scene light intensity with it.
     */
    private fun changeIndirectLightByHdrUrl(call: MethodCall, result: MethodChannel.Result) {
        val url: String? = getValue(call, changeIndirectLightByHdrUrlKey)
        val intensity: Double? = getValue(call, changeIndirectLightByHdrUrlIntensityKey)

        coroutineScope.launch {

            when (val resource = modelViewer?.changeIndirectLightFromHdrUrl(url, intensity)) {
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
    private fun changeIndirectLightByDefaultIndirectLight(call: MethodCall, result: MethodChannel.Result) {
        val light = getValue<Map<String?, Any?>>(call, changeIndirectLightByDefaultIndirectLightKey)?.toObject<DefaultIndirectLight>()
        when (val resource = modelViewer?.changeIndirectLightByDefaultIndirectLight(light)) {
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
    private fun changeToDefaultIndirectLight(result: MethodChannel.Result) {
        if (modelViewer != null) {
            modelViewer.changeToDefaultIndirectLight()
            result.success("Default light intensity changed")
        } else {
            result.error("Model viewer isn't initialized.", "Model viewer isn't initialized.", null)
        }
    }

    /**
     * change scene direct light by light class
     * by different types ,intensity, position, etc .
     * it takes light object as an argument.
     * and update the scene  light with it.
     */
    private fun changeSceneLight(call: MethodCall, result: MethodChannel.Result) {
        val light = getValue<Map<String?, Any?>>(call, changeLightKey)?.toObject<Light>()
        when (val resource = modelViewer?.changeLight(light)) {
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
    private fun changeToDefaultLight(result: MethodChannel.Result) {
        if (modelViewer != null) {
           return modelViewer.changeToDefaultLight()
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
     * Load glb model from url.
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


    /**
     *change current model scale.
     * it takes an Float? scale as an argument.
     * and updates current model scale.
     * if intensity is provide, it will update the scene light intensity with it.
     */
    private fun changeModelScale(call: MethodCall, result: MethodChannel.Result) {
        val scale: Float? = getValue<Double>(call, changeModelScaleKey)?.toFloat()
        coroutineScope.launch {
            when (val resource = modelViewer?.changeModelScale(scale)) {
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
     *change current model position.
     * it takes an FloatArray  of [x,y,z] coordinates as an argument.
     * and updates current model center position.
     */
    private fun changeModelPosition(call: MethodCall, result: MethodChannel.Result) {
        val position = getValue<Map<String?, Any?>>(call, changeModelPositionKey)?.toObject<Position>()
        coroutineScope.launch {
            when (val resource = modelViewer?.changeModelPosition(position)) {
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
     *updateCamera.
     * it takes an camera object as an argument.
     * and updates current camera.
     */
    private fun updateCamera(call: MethodCall, result: MethodChannel.Result) {
        val cameraInfo = getValue<Map<String?, Any?>>(call, updateCameraKey)?.toObject<Camera>()
        coroutineScope.launch {
            when (val resource = modelViewer?.updateCamera(cameraInfo)) {
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
     *update Exposure : updates current camera exposure.
     * it takes an Exposure object as an argument.
     * and updates current camera exposure.
     */
    private fun updateExposure(call: MethodCall, result: MethodChannel.Result) {
        val exposure = getValue<Map<String?, Any?>>(call, updateExposureKey)?.toObject<Exposure>()
        coroutineScope.launch {
            when (val resource = modelViewer?.updateExposure(exposure)) {
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
     *updateProjection.
     * it takes a projection object as an argument.
     * and updates current camera projection.
     */
    private fun updateProjection(call: MethodCall, result: MethodChannel.Result) {
        val projection = getValue<Map<String?, Any?>>(call, updateProjectionKey)?.toObject<Projection>()
        coroutineScope.launch {
            when (val resource = modelViewer?.updateProjection(projection)) {
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
     *updateLensProjection.
     * it takes an lens projection object as an argument.
     * and updates current model center position.
     */
    private fun updateLensProjection(call: MethodCall, result: MethodChannel.Result) {
        val lensProjection = getValue<Map<String?, Any?>>(call, updateLensProjectionKey)?.toObject<LensProjection>()
        coroutineScope.launch {
            when (val resource = modelViewer?.updateLensProjection(lensProjection)) {
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
     *update Camera Shift.
     * it takes an double array  of [x,y] coordinates as an argument.
     * and updates current camera shift.
     */
    private fun updateCameraShift(call: MethodCall, result: MethodChannel.Result) {
        val shift: DoubleArray? = getValue<List<Double>>(call, updateCameraShiftKey)?.toDoubleArray()
        coroutineScope.launch {
            when (val resource = modelViewer?.updateCameraShift(shift)) {
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
     *updateCameraScaling.
     * it takes an double array  of [x,y] coordinates as an argument.
     * and updates current camera scaling.
     */
    private fun updateCameraScaling(call: MethodCall, result: MethodChannel.Result) {
        val scaling: DoubleArray? = getValue<List<Double>>(call, updateCameraScalingKey)?.toDoubleArray()
        coroutineScope.launch {
            when (val resource = modelViewer?.updateCameraScaling(scaling)) {
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
     *setDefaultCamera.
     */
    private fun setDefaultCamera( result: MethodChannel.Result) {
        coroutineScope.launch {
            when (val resource = modelViewer?.setDefaultCamera()) {
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



    private fun lookAtCameraPosition(call: MethodCall, result: MethodChannel.Result) {

        val eyeArray: DoubleArray? = getValue<List<Double>>(call, eyeArrayKey)?.toDoubleArray()
        val targetArray: DoubleArray? = getValue<List<Double>>(call, targetArrayKey)?.toDoubleArray()
        val upwardArray: DoubleArray? = getValue<List<Double>>(call, upwardArrayKey)?.toDoubleArray()

        coroutineScope.launch {
            when (val resource = modelViewer?.lookAtPosition(eyeArray,targetArray,upwardArray)) {
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
     *lookAtDefault camera Position.
     */
    private fun lookAtDefaultPosition( result: MethodChannel.Result) {
        coroutineScope.launch {
            when (val resource = modelViewer?.lookAtDefaultPosition()) {
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
     *lookAtDefault camera Position.
     */
    private fun getCameraLookAtPositions( result: MethodChannel.Result) {
        coroutineScope.launch {
            when (val resource = modelViewer?.getLookAt()) {
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



    private fun scrollCameraTo(call: MethodCall, result: MethodChannel.Result) {

        val x: Int? = getValue(call, cameraScrollXKey)
        val y: Int? = getValue(call, cameraScrollYKey)
        val scrollDelta: Float? = getValue<Double>(call, cameraScrollDeltaKey)?.toFloat()

        coroutineScope.launch {
            when (val resource = modelViewer?.scroll(x,y,scrollDelta)) {
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


    private fun beginCameraGrab(call: MethodCall, result: MethodChannel.Result) {

        val x: Int? = getValue(call, cameraGrabBeginXKey)
        val y: Int? = getValue(call, cameraGrabBeginYKey)
        val strafe = getValue<Boolean>(call, cameraGrabBeginStrafeKey)

        coroutineScope.launch {
            when (val resource = modelViewer?.grabBegin(x,y,strafe)) {
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


    private fun updateCameraGrab(call: MethodCall, result: MethodChannel.Result) {

        val x: Int? = getValue(call, cameraGrabUpdateXKey)
        val y: Int? = getValue(call, cameraGrabUpdateYKey)

        coroutineScope.launch {
            when (val resource = modelViewer?.grabUpdate(x,y)) {
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

    private fun endCameraGrab(result: MethodChannel.Result) {
        coroutineScope.launch {
            when (val resource = modelViewer?.grabEnd()) {
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
    private fun getCameraRayCast(call: MethodCall, result: MethodChannel.Result) {

        val x: Int? = getValue(call, cameraRayCastXKey)
        val y: Int? = getValue(call, cameraRayCastYKey)

        coroutineScope.launch {
            when (val resource = modelViewer?.raycast(x,y)) {
                is Resource.Success -> {
                    val data = resource.data?.toList()?.map { it.toDouble() }
                    result.success(data)
                }
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


    /**
     *update ground.
     * it takes an Ground object as an argument.
     * and updates current camera.
     */
    private fun updateGround(call: MethodCall, result: MethodChannel.Result) {
        val ground = getValue<Map<String?, Any?>>(call, updateGroundKey)?.toObject<Ground>()
        coroutineScope.launch {
            when (val resource = modelViewer?.updateGround(ground)) {
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
     *update ground.
     * it takes an Ground object as an argument.
     * and updates current camera.
     */
    private fun updateGroundMaterial(call: MethodCall, result: MethodChannel.Result) {
        val material = getValue<Map<String?, Any?>>(call, updateGroundMaterialKey)?.toObject<Material>()
        coroutineScope.launch {
            when (val resource = modelViewer?.updateGroundMaterial(material)) {
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
     *add Shape.
     * it takes an shape object as an argument.
     * and adds the shape to current rendered shapes.
     */
    private fun addShape(call: MethodCall, result: MethodChannel.Result) {
        val shape = getValue<Map<String?, Any?>>(call, addShapeKey)?.toObject<Shape>()
        coroutineScope.launch {
            when (val resource = modelViewer?.addShape(shape)) {
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
     *remove Shape.
     * it takes an shape id  as an argument.
     * and removes the shape to current rendered shapes.
     */
    private fun removeShape(call: MethodCall, result: MethodChannel.Result) {
        val id = getValue<Int>(call, removeShapeKey)

        coroutineScope.launch {
            when (val resource = modelViewer?.removeShape(id)) {
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
     *add Shape.
     * it takes an shape object as an argument.
     * and adds the shape to current rendered shapes.
     */
    private fun updateShape(call: MethodCall, result: MethodChannel.Result) {
        val shape = getValue<Map<String?, Any?>>(call, updateShapeKey)?.toObject<Shape>()
        val id = getValue<Int>(call, updateShapeIdKey)

        coroutineScope.launch {
            when (val resource = modelViewer?.updateShape(id, shape)) {
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

    private fun getCurrentCreatedShapesIds( result: MethodChannel.Result) {
        if(modelViewer != null) {
            val ids = modelViewer.getCreatedShapesIds()
            result.success(ids)
        }else{
            result.error(
                    "Model viewer isn't initialized.",
                    "Model viewer isn't initialized.",
                    null
                )
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
        private const val getAnimationNameByIndexKey = "GET_ANIMATION_NAME_BY_INDEX_KEY"

        private const val getAnimationCount = "GET_ANIMATION_COUNT"

        private  const val getCurrentAnimationIndex = "GET_CURRENT_ANIMATION_INDEX"

        private  const val changeSkyboxByAsset = "CHANGE_SKYBOX_BY_ASSET"
        private  const val changeSkyboxByAssetKey = "CHANGE_SKYBOX_BY_ASSET_KEY"

        private const val changeSkyboxByUrl = "CHANGE_SKYBOX_BY_URL"
        private const val changeSkyboxByUrlKey = "CHANGE_SKYBOX_BY_URL_KEY"


        private const val changeSkyboxByHdrAsset = "CHANGE_SKYBOX_BY_HDR_ASSET"
        private const val changeSkyboxByHdrAssetKey ="CHANGE_SKYBOX_BY_HDR_ASSET_KEY"

        private const val changeSkyboxByHdrUrl = "CHANGE_SKYBOX_BY_HDR_URL"
        private const val changeSkyboxByHdrUrlKey = "CHANGE_SKYBOX_BY_HDR_URL_KEY"


        private const val changeSkyboxColor = "CHANGE_SKYBOX_COLOR"
        private  const val changeSkyboxColorKey = "CHANGE_SKYBOX_COLOR_KEY"

        private  const val changeToTransparentSkybox = "CHANGE_TO_TRANSPARENT_SKYBOX"

        private const val changeIndirectLightByKtxAsset = "CHANGE_LIGHT_BY_ASSET"
        private const val changeIndirectLightByKtxAssetKey = "CHANGE_LIGHT_BY_ASSET_KEY"
        private const val changeIndirectLightByKtxAssetIntensityKey = "CHANGE_LIGHT_BY_ASSET_INTENSITY_KEY"

        private const val changeIndirectLightByKtxUrl = "CHANGE_LIGHT_BY_KTX_URL"
        private const val changeIndirectLightByKtxUrlKey = "CHANGE_LIGHT_BY_KTX_URL_KEY"
        private const val changeIndirectLightByKtxUrlIntensityKey = "CHANGE_LIGHT_BY_KTX_URL_INTENSITY_KEY"


        private const val changeIndirectLightByHdrAsset = "CHANGE_LIGHT_BY_HDR_ASSET"
        private const val changeIndirectLightByHdrAssetKey = "CHANGE_LIGHT_BY_HDR_ASSET_KEY"
        private const val changeIndirectLightByHdrAssetIntensityKey = "CHANGE_LIGHT_BY_HDR_ASSET_INTENSITY_KEY"

        private const val changeIndirectLightByHdrUrl = "CHANGE_LIGHT_BY_HDR_URL"
        private const val changeIndirectLightByHdrUrlKey = "CHANGE_LIGHT_BY_HDR_URL_KEY"
        private const val changeIndirectLightByHdrUrlIntensityKey = "CHANGE_LIGHT_BY_HDR_URL_INTENSITY_KEY"


        private const val changeIndirectLightByDefaultIndirectLight = "CHANGE_LIGHT_BY_INDIRECT_LIGHT"
        private const val changeIndirectLightByDefaultIndirectLightKey = "CHANGE_LIGHT_BY_INDIRECT_LIGHT_KEY"

        private const val changeToDefaultIndirectLight = "CHANGE_TO_DEFAULT_LIGHT_INTENSITY"

        private const val changeLight="CHANGE_LIGHT"
        private const val changeLightKey="CHANGE_LIGHT_KEY"

        private const val changeToDefaultLight = "CHANGE_TO_DEFAULT_LIGHT"


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
       private const val getCurrentModelState = "GET_CURRENT_MODEL_STATE"

        private const val changeModelScale ="CHANGE_MODEL_SCALE"
        private const val changeModelScaleKey ="CHANGE_MODEL_SCALE_KEY"
        private const val changeModelPosition ="CHANGE_MODEL_POSITION"
        private const val changeModelPositionKey ="CHANGE_MODEL_POSITION_KEY"


        private const val updateCamera ="UPDATE_CAMERA"
        private const val updateCameraKey ="UPDATE_CAMERA_KEY"

        private const val updateExposure ="UPDATE_EXPOSURE"
        private const val updateExposureKey ="UPDATE_EXPOSURE_KEY"

        private const val updateProjection ="UPDATE_PROJECTION"
        private const val updateProjectionKey ="UPDATE_PROJECTION_KEY"

        private const val updateLensProjection ="UPDATE_LENS_PROJECTION"
        private const val updateLensProjectionKey ="UPDATE_LENS_PROJECTION_KEY"

        private const val updateCameraShift ="UPDATE_CAMERA_SHIFT"
        private const val updateCameraShiftKey ="UPDATE_CAMERA_SHIFT_KEY"

        private const val updateCameraScaling ="UPDATE_CAMERA_SCALING"
        private const val updateCameraScalingKey="UPDATE_CAMERA_SCALING_KEY"

        private const val setDefaultCamera ="SET_DEFAULT_CAMERA"

        private const val lookAtDefaultPosition ="LOOK_AT_DEFAULT_POSITION"

        private const val lookAtPosition ="LOOK_AT_POSITION"
        private const val eyeArrayKey ="EYE_ARRAY_KEY"
        private const val targetArrayKey ="TARGET_ARRAY_KEY"
        private const val upwardArrayKey ="UPWARD_ARRAY_KEY"

        private const val getLookAt ="GET_LOOK_AT"
        private const val cameraScroll ="CAMERA_SCROLL"

        private const val cameraScrollXKey ="CAMERA_SCROLL_X_KEY"
        private const val cameraScrollYKey ="CAMERA_SCROLL_Y_KEY"
        private const val cameraScrollDeltaKey ="CAMERA_SCROLL_DELTA_KEY"

        private const val cameraRayCast="CAMERA_RAYCAST"
        private const val cameraRayCastXKey="CAMERA_RAYCAST_X_KEY"
        private const val cameraRayCastYKey="CAMERA_RAYCAST_Y_KEY"

        private const val cameraGrabBegin = "CAMERA_GRAB_BEGIN"
        private const val cameraGrabBeginXKey = "CAMERA_GRAB_BEGIN_X_KEY"
        private const val cameraGrabBeginYKey = "CAMERA_GRAB_BEGIN_Y_KEY"
        private const val cameraGrabBeginStrafeKey = "CAMERA_GRAB_BEGIN_STRAFE_KEY"


        private const val cameraGrabUpdate = "CAMERA_GRAB_UPDATE"
        private const val cameraGrabUpdateXKey = "CAMERA_GRAB_UPDATE_X_KEY"
        private const val cameraGrabUpdateYKey = "CAMERA_GRAB_UPDATE_Y_KEY"
        private const val cameraGrabEnd = "CAMERA_GRAB_END"


        private const val updateGround = "UPDATE_GROUND"
        private const val updateGroundKey = "UPDATE_GROUND_KEY"

        private const val updateGroundMaterial = "UPDATE_GROUND_MATERIAL"
        private const val updateGroundMaterialKey = "UPDATE_GROUND_MATERIAL_KEY"

        private const val addShape = "ADD_SHAPE"
        private const val addShapeKey = "ADD_SHAPE_KEY"
        private const val removeShape = "REMOVE_SHAPE"
        private const val removeShapeKey = "REMOVE_SHAPE_KEY"
        private const val updateShape = "UPDATE_SHAPE"
        private const val updateShapeKey = "UPDATE_SHAPE_KEY"
        private const val updateShapeIdKey = "UPDATE_SHAPE_ID_KEY"

        private const val getCurrentCreatedShapesIds = "CREATED_SHAPES_IDS"




    }


}