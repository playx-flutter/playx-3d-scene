import 'package:flutter/services.dart';
import 'package:playx_3d_scene/models/scene/camera/camera.dart';
import 'package:playx_3d_scene/models/scene/camera/exposure.dart';
import 'package:playx_3d_scene/models/scene/camera/lens_projection.dart';
import 'package:playx_3d_scene/models/scene/camera/projection.dart';
import 'package:playx_3d_scene/models/scene/ground.dart';
import 'package:playx_3d_scene/models/scene/indirect_light/indirect_light.dart';
import 'package:playx_3d_scene/models/scene/light/light.dart';
import 'package:playx_3d_scene/models/scene/material/material.dart';
import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';
import 'package:playx_3d_scene/models/state/model_state.dart';

class Playx3dSceneController {
  int id;
  late MethodChannel _channel;

  static const String _channelName = "io.sourcya.playx.3d.scene.channel";

  Playx3dSceneController({required this.id}) {
    _channel = MethodChannel('${_channelName}_$id');
  }

  //animation

  /// Changes the current animation by index.
  /// it takes an Int? index as an argument.
  /// and returns the new animation index.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<int?> changeAnimationByIndex(int? index) => _channel.invokeMethod<int>(
        _changeAnimationByIndex,
        {_changeAnimationByIndexKey: index},
      );

  /// Changes the current animation by animation name.
  /// it takes an String? animation name as an argument.
  /// and returns the new animation index.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<int?> changeAnimationByName(String? animationName) =>
      _channel.invokeMethod<int>(
        _changeAnimationByName,
        {_changeAnimationByNameKey: animationName},
      );

  /// Get current model animation names.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<List<String?>?> getAnimationNames() =>
      _channel.invokeMethod<List<Object?>>(
        _getAnimationNames,
        {},
      ).then((value) {
        return value?.map((e) => e as String?).toList();
      });

  /// Get current model animation count.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<int?> getAnimationCount() => _channel.invokeMethod<int>(
        _getAnimationCount,
        {},
      );

  /// Get current animation index.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<int?> getCurrentAnimationIndex() => _channel.invokeMethod<int>(
        _getCurrentAnimationIndex,
        {},
      );

  /// Get Animation name by given index.
  /// it takes an Int? index as an argument.
  /// and returns the animation name.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> getAnimationNameByIndex(int? index) =>
      _channel.invokeMethod<String>(
        _getAnimationNameByIndex,
        {_getAnimationNameByIndexKey: index},
      );

  //skybox

  /// change environment by given asset path.
  /// it takes an String? asset path as an argument.
  /// should be provided with the KTX skybox file.
  /// so it can update the environment skybox with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeSkyboxByKtxAsset(String? path) =>
      _channel.invokeMethod<String>(
        _changeSkyboxByAsset,
        {_changeSkyboxByAssetKey: path},
      );

  /// change environment by given asset path.
  /// it takes an String? asset path as an argument.
  /// should be provided with the KTX skybox file.
  /// so it can update the environment skybox with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeSkyboxByKtxUrl(String? url) =>
      _channel.invokeMethod<String>(
        _changeSkyboxByUrl,
        {_changeSkyboxByUrlKey: url},
      );

  /// change skybox by given asset path.
  /// it takes an String? asset path as an argument.
  /// should be provided with the HDR file.
  /// so it can update the environment skybox with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeSkyboxByHdrAsset(String? path) =>
      _channel.invokeMethod<String>(
        _changeSkyboxByHdrAsset,
        {_changeSkyboxByHdrAssetKey: path},
      );

  /// change skybox by given asset path.
  /// it takes an String? asset path as an argument.
  /// should be provided with the the HDR file.
  /// so it can update the environment skybox with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeSkyboxByHdrUrl(String? url) =>
      _channel.invokeMethod<String>(
        _changeSkyboxByHdrUrl,
        {_changeSkyboxByHdrUrlKey: url},
      );

  /// change environment by given color.
  /// it takes an Color?  as an argument.
  /// and updates the environment skybox color
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeSkyboxColor(Color? color) {
    final environmentColor = color?.toHex;
    return _channel.invokeMethod<String>(
      _changeSkyboxColor,
      {_changeSkyboxColorKey: environmentColor},
    );
  }

  /// change environment to be transparent.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeToTransparentSkybox() => _channel.invokeMethod<String>(
        _changeToTransparentSkybox,
        {},
      );

  //light

  /// change scene indirect light by given asset path.
  /// it takes an String? asset path as an argument.
  /// and can take light intensity as an argument.
  /// should be provided with the KTX image based lighting file.
  /// so it can update the scene light with it.
  /// if intensity is provide, it will update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeIndirectLightByKtxAsset(
          {required String? path, double? intensity}) =>
      _channel.invokeMethod<String>(
        _changeLightByKtxAsset,
        {
          _changeLightByKtxAssetKey: path,
          _changeLightByKtxAssetIntensityKey: intensity
        },
      );

  /// change scene indirect light by given ktx file url .
  /// it takes an String? asset path as an argument.
  /// and can take light intensity as an argument.
  /// should be provided with the KTX image based lighting file.
  /// so it can update the scene light with it.
  /// if intensity is provide, it will update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeIndirectLightByKtxUrl(
          {required String? url, double? intensity}) =>
      _channel.invokeMethod<String>(
        _changeLightByKtxUrl,
        {
          _changeLightByKtxUrlKey: url,
          _changeLightByKtxUrlIntensityKey: intensity
        },
      );

  /// change scene indirect light by given HDR asset path.
  /// it takes an String? asset path as an argument.
  /// and can take light intensity as an argument.
  /// should be provided with the HDR image based lighting file.
  /// so it can update the scene light with it.
  /// if intensity is provide, it will update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeIndirectLightByHdrAsset(
          {required String? path, double? intensity}) =>
      _channel.invokeMethod<String>(
        _changeLightByHdrAsset,
        {
          _changeLightByHdrAssetKey: path,
          _changeLightByHdrAssetIntensityKey: intensity
        },
      );

  /// change scene indirect light by given Hdr file url .
  /// it takes an String? asset path as an argument.
  /// and can take light intensity as an argument.
  /// should be provided with the HDR image based lighting file.
  /// so it can update the scene light with it.
  /// if intensity is provide, it will update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeIndirectLightByHdrUrl(
          {required String? url, double? intensity}) =>
      _channel.invokeMethod<String>(
        _changeLightByHdrUrl,
        {
          _changeLightByHdrUrlKey: url,
          _changeLightByHdrUrlIntensityKey: intensity
        },
      );

  /// change scene indirect light by given intensity.
  /// it takes light intensity as an argument.
  /// and update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeIndirectLightByDefaultIndirectLight(
          IndirectLight indirectLight) =>
      _channel.invokeMethod<String>(
        _changeLightByIndirectLight,
        {_changeLightByIndirectLightKey: indirectLight.toJson()},
      );

  /// change scene indirect light to the default intensity which is 40_000.0.
  /// with radiance bands of 1.0, radiance sh of [1,1,1]
  /// and irradiance bands of 1.0, irradiance sh of [1,1,1]
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeToDefaultIndirectLight() =>
      _channel.invokeMethod<String>(
        _changeToDefaultIndirectLight,
        {},
      );

  /// change scene light by given intensity.
  /// it takes light light as an argument.
  /// and update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeSceneLight(Light light) =>
      _channel.invokeMethod<String>(
        _changeLight,
        {_changeLightKey: light.toJson()},
      );

  /// change scene indirect light to the default intensity which is 100_000.0.
  /// with color temperature bands of 6_500.0,
  /// and direction of [0.0, -1.0f, 0.0f],
  /// and cast shadows true.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeToDefaultLight() => _channel.invokeMethod<String>(
        _changeToDefaultLight,
        {},
      );

  //load model

  /// Load glb model from assets.
  /// it takes asset path as an argument.
  /// and update the current model with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> loadGlbModelFromAssets(String? path) =>
      _channel.invokeMethod<String>(
        _loadGlbModelFromAssets,
        {_loadGlbModelFromAssetsPathKey: path},
      );

  /// Load glb model from url.
  /// it takes url as an argument.
  /// and update the current model with it.
  /// and returns a message whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> loadGlbModelFromUrl(String? url) =>
      _channel.invokeMethod<String>(
        _loadGlbModelFromUrl,
        {_loadGlbModelFromUrlKey: url},
      );

  /// Load gltf  model from assets.
  /// it takes asset path as an argument.
  /// and prefix for gltf image assets.
  /// if the images path that in the gltf file different from the flutter asset path,
  /// you can add prefix to the images path to be before the image.
  /// LIKE if in the gltf file, the image path is textures/texture.png
  /// and in assets the image path is assets/models/textures/texture.png
  /// you will need to add prefix to be 'assets/models/'.
  ///and postfix path for gltf image assets.
  /// if the images path that in the gltf file different from the flutter asset path,
  /// you can add postfix to the images path to be after the image.
  /// LIKE if in the gltf file, the image path is assets/textures/texture
  /// and in assets the image path is assets/textures/texture.png
  /// you will need to add prefix to be '.png'.
  /// and update the current model with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> loadGltfModelFromAssets(String? path,
          {String? imagePathPrefix, String? imagePathPostfix}) =>
      _channel.invokeMethod<String>(
        _loadGltfModelFromAssets,
        {
          _loadGltfModelFromAssetsPathKey: path,
          _loadGltfModelFromAssetsPrefixPathKey: imagePathPrefix,
          _loadGltfModelFromAssetsPostfixPathKey: imagePathPostfix
        },
      );

  /// change model scale.
  /// it takes scale as an argument.
  /// and update the current model scale.
  /// and returns a message whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeModelScale(double? scale) =>
      _channel.invokeMethod<String>(
        _changeModelScale,
        {_changeModelScaleKey: scale},
      );

  /// change model center position.
  /// it takes list of double of [x,y,z] coordinates as an argument.
  /// and update the current model center position.
  /// and returns a message whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeModelCenterPosition(List<double>? centerPosition) =>
      _channel.invokeMethod<String>(
        _changeModelPosition,
        {_changeModelPositionKey: centerPosition},
      );

  /// Get current model state.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<ModelState> getCurrentModelState() async {
    try {
      final state = await _channel.invokeMethod<String>(
        _getCurrentModelState,
        {},
      );
      return ModelState.from(state);
    } catch (error) {
      return ModelState.none;
    }
  }

  Future<String?> updateCamera(Camera? camera) => _channel.invokeMethod<String>(
        _updateCamera,
        {_updateCameraKey: camera?.toJson()},
      );

  Future<String?> updateExposure(Exposure? exposure) =>
      _channel.invokeMethod<String>(
        _updateExposure,
        {_updateExposureKey: exposure?.toJson()},
      );

  Future<String?> updateProjection(Projection? projection) =>
      _channel.invokeMethod<String>(
        _updateProjection,
        {_updateProjectionKey: projection?.toJson()},
      );

  Future<String?> updateLensProjection(LensProjection? lensProjection) =>
      _channel.invokeMethod<String>(
        _updateLensProjection,
        {_updateLensProjectionKey: lensProjection?.toJson()},
      );

  Future<String?> updateCameraShift(List<double>? shift) =>
      _channel.invokeMethod<String>(
        _updateCameraShift,
        {_updateCameraShiftKey: shift},
      );

  Future<String?> updateCameraScaling(List<double>? scaling) =>
      _channel.invokeMethod<String>(
        _updateCameraScaling,
        {_updateCameraScalingKey: scaling},
      );

  Future<String?> setDefaultCamera() => _channel.invokeMethod<String>(
        _setDefaultCamera,
        {},
      );

  Future<String?> lookAtDefaultCameraPosition() =>
      _channel.invokeMethod<String>(
        _lookAtDefaultPosition,
        {},
      );

  Future<List<double>?> getCameraLookAtPositions() async {
    final positions = await _channel.invokeMethod<List<Object?>?>(
      _getLookAt,
      {},
    );
    return positions?.map((e) => (e as double)).toList();
  }

  Future<String?> lookAtCameraPosition({
    List<double>? eyePos,
    List<double>? targetPos,
    List<double>? upwardPos,
  }) =>
      _channel.invokeMethod<String>(
        _lookAtPosition,
        {
          _eyeArrayKey: eyePos,
          _targetArrayKey: targetPos,
          _upwardArrayKey: upwardPos
        },
      );

  Future<String?> scrollCameraTo({
    num? x,
    num? y,
    double? scrollDelta,
  }) =>
      _channel.invokeMethod<String>(
        _cameraScroll,
        {
          _cameraScrollXKey: x,
          _cameraScrollYKey: y,
          _cameraScrollDeltaKey: scrollDelta
        },
      );

  Future<String?> beginCameraGrab({
    num? x,
    num? y,
    bool? strafe,
  }) =>
      _channel.invokeMethod<String>(
        _cameraGrabBegin,
        {
          _cameraGrabBeginXKey: x,
          _cameraGrabBeginYKey: y,
          _cameraGrabBeginStrafeKey: strafe
        },
      );

  Future<String?> updateCameraGrab({
    num? x,
    num? y,
  }) =>
      _channel.invokeMethod<String>(
        _cameraGrabUpdate,
        {
          _cameraGrabUpdateXKey: x,
          _cameraGrabUpdateYKey: y,
        },
      );

  Future<String?> endCameraGrab() => _channel.invokeMethod<String>(
        _cameraGrabEnd,
        {},
      );

  Future<String?> getCameraRayCast({
    num? x,
    num? y,
  }) =>
      _channel.invokeMethod<String>(
        _cameraRayCast,
        {
          _cameraRayCastXKey: x,
          _cameraRayCastYKey: y,
        },
      );

  Future<String?> updateGround(Ground? ground) => _channel.invokeMethod<String>(
        _updateGround,
        {_updateGroundKey: ground?.toJson()},
      );

  Future<String?> updateGroundMaterial(PlayxMaterial? material) =>
      _channel.invokeMethod<String>(
        _updateGroundMaterial,
        {_updateGroundMaterialKey: material?.toJson()},
      );
}

const String _changeAnimationByIndex = "CHANGE_ANIMATION_BY_INDEX";
const String _changeAnimationByIndexKey = "CHANGE_ANIMATION_BY_INDEX_KEY";

const String _changeAnimationByName = "CHANGE_ANIMATION_BY_NAME";
const String _changeAnimationByNameKey = "CHANGE_ANIMATION_BY_NAME_KEY";
const String _getAnimationNames = "GET_ANIMATION_NAMES";

const String _getAnimationNameByIndex = "GET_ANIMATION_NAME_BY_INDEX";
const String _getAnimationNameByIndexKey = "GET_ANIMATION_NAME_BY_INDEX_KEY";

const String _getAnimationCount = "GET_ANIMATION_COUNT";

const String _getCurrentAnimationIndex = "GET_CURRENT_ANIMATION_INDEX";

const String _changeSkyboxByAsset = "CHANGE_SKYBOX_BY_ASSET";
const String _changeSkyboxByAssetKey = "CHANGE_SKYBOX_BY_ASSET_KEY";

const String _changeSkyboxByUrl = "CHANGE_SKYBOX_BY_URL";
const String _changeSkyboxByUrlKey = "CHANGE_SKYBOX_BY_URL_KEY";

const String _changeSkyboxByHdrAsset = "CHANGE_SKYBOX_BY_HDR_ASSET";
const String _changeSkyboxByHdrAssetKey = "CHANGE_SKYBOX_BY_HDR_ASSET_KEY";

const String _changeSkyboxByHdrUrl = "CHANGE_SKYBOX_BY_HDR_URL";
const String _changeSkyboxByHdrUrlKey = "CHANGE_SKYBOX_BY_HDR_URL_KEY";

const String _changeSkyboxColor = "CHANGE_SKYBOX_COLOR";
const String _changeSkyboxColorKey = "CHANGE_SKYBOX_COLOR_KEY";

const String _changeToTransparentSkybox = "CHANGE_TO_TRANSPARENT_SKYBOX";

const String _changeLightByKtxAsset = "CHANGE_LIGHT_BY_ASSET";
const String _changeLightByKtxAssetKey = "CHANGE_LIGHT_BY_ASSET_KEY";
const String _changeLightByKtxAssetIntensityKey =
    "CHANGE_LIGHT_BY_ASSET_INTENSITY_KEY";

const String _changeLightByKtxUrl = "CHANGE_LIGHT_BY_KTX_URL";
const String _changeLightByKtxUrlKey = "CHANGE_LIGHT_BY_KTX_URL_KEY";
const String _changeLightByKtxUrlIntensityKey =
    "CHANGE_LIGHT_BY_KTX_URL_INTENSITY_KEY";

const String _changeLightByHdrAsset = "CHANGE_LIGHT_BY_HDR_ASSET";
const String _changeLightByHdrAssetKey = "CHANGE_LIGHT_BY_HDR_ASSET_KEY";
const String _changeLightByHdrAssetIntensityKey =
    "CHANGE_LIGHT_BY_HDR_ASSET_INTENSITY_KEY";

const String _changeLightByHdrUrl = "CHANGE_LIGHT_BY_HDR_URL";
const String _changeLightByHdrUrlKey = "CHANGE_LIGHT_BY_HDR_URL_KEY";
const String _changeLightByHdrUrlIntensityKey =
    "CHANGE_LIGHT_BY_HDR_URL_INTENSITY_KEY";

const String _changeLightByIndirectLight = "CHANGE_LIGHT_BY_INDIRECT_LIGHT";
const String _changeLightByIndirectLightKey =
    "CHANGE_LIGHT_BY_INDIRECT_LIGHT_KEY";

const String _changeToDefaultIndirectLight =
    "CHANGE_TO_DEFAULT_LIGHT_INTENSITY";

const String _changeLight = "CHANGE_LIGHT";
const String _changeLightKey = "CHANGE_LIGHT_KEY";
const String _changeToDefaultLight = "CHANGE_TO_DEFAULT_LIGHT";

const String _loadGlbModelFromAssets = "LOAD_GLB_MODEL_FROM_ASSETS";
const String _loadGlbModelFromAssetsPathKey =
    "LOAD_GLB_MODEL_FROM_ASSETS_PATH_KEY";

const String _loadGlbModelFromUrl = "LOAD_GLB_MODEL_FROM_URL";
const String _loadGlbModelFromUrlKey = "LOAD_GLB_MODEL_FROM_URL_KEY";

const String _loadGltfModelFromAssets = "LOAD_GLTF_MODEL_FROM_ASSETS";
const String _loadGltfModelFromAssetsPathKey =
    "LOAD_GLTF_MODEL_FROM_ASSETS_PATH_KEY";
const String _loadGltfModelFromAssetsPrefixPathKey =
    "LOAD_GLTF_MODEL_FROM_ASSETS_PREFIX_PATH_KEY";
const String _loadGltfModelFromAssetsPostfixPathKey =
    "LOAD_GLTF_MODEL_FROM_ASSETS_POSTFIX_PATH_KEY";

const String _getCurrentModelState = "GET_CURRENT_MODEL_STATE";
const String _changeModelScale = "CHANGE_MODEL_SCALE";
const String _changeModelScaleKey = "CHANGE_MODEL_SCALE_KEY";
const String _changeModelPosition = "CHANGE_MODEL_POSITION";
const String _changeModelPositionKey = "CHANGE_MODEL_POSITION_KEY";
const String _updateCamera = "UPDATE_CAMERA";
const String _updateCameraKey = "UPDATE_CAMERA_KEY";

const String _updateExposure = "UPDATE_EXPOSURE";
const String _updateExposureKey = "UPDATE_EXPOSURE_KEY";

const String _updateProjection = "UPDATE_PROJECTION";
const String _updateProjectionKey = "UPDATE_PROJECTION_KEY";

const String _updateLensProjection = "UPDATE_LENS_PROJECTION";
const String _updateLensProjectionKey = "UPDATE_LENS_PROJECTION_KEY";

const String _updateCameraShift = "UPDATE_CAMERA_SHIFT";
const String _updateCameraShiftKey = "UPDATE_CAMERA_SHIFT_KEY";

const String _updateCameraScaling = "UPDATE_CAMERA_SCALING";
const String _updateCameraScalingKey = "UPDATE_CAMERA_SCALING_KEY";

const String _setDefaultCamera = "SET_DEFAULT_CAMERA";
const String _lookAtDefaultPosition = "LOOK_AT_DEFAULT_POSITION";

const String _lookAtPosition = "LOOK_AT_POSITION";
const String _eyeArrayKey = "EYE_ARRAY_KEY";
const String _targetArrayKey = "TARGET_ARRAY_KEY";
const String _upwardArrayKey = "UPWARD_ARRAY_KEY";

const String _getLookAt = "GET_LOOK_AT";
const String _cameraScroll = "CAMERA_SCROLL";

const String _cameraScrollXKey = "CAMERA_SCROLL_X_KEY";
const String _cameraScrollYKey = "CAMERA_SCROLL_Y_KEY";
const String _cameraScrollDeltaKey = "CAMERA_SCROLL_DELTA_KEY";

const String _cameraRayCast = "CAMERA_RAYCAST";
const String _cameraRayCastXKey = "CAMERA_RAYCAST_X_KEY";
const String _cameraRayCastYKey = "CAMERA_RAYCAST_Y_KEY";

const String _cameraGrabBegin = "CAMERA_GRAB_BEGIN";
const String _cameraGrabBeginXKey = "CAMERA_GRAB_BEGIN_X_KEY";
const String _cameraGrabBeginYKey = "CAMERA_GRAB_BEGIN_Y_KEY";
const String _cameraGrabBeginStrafeKey = "CAMERA_GRAB_BEGIN_STRAFE_KEY";

const String _cameraGrabUpdate = "CAMERA_GRAB_UPDATE";
const String _cameraGrabUpdateXKey = "CAMERA_GRAB_UPDATE_X_KEY";
const String _cameraGrabUpdateYKey = "CAMERA_GRAB_UPDATE_Y_KEY";
const String _cameraGrabEnd = "CAMERA_GRAB_END";

const String _updateGround = "UPDATE_GROUND";
const String _updateGroundKey = "UPDATE_GROUND_KEY";

const String _updateGroundMaterial = "UPDATE_GROUND_MATERIAL";
const String _updateGroundMaterialKey = "UPDATE_GROUND_MATERIAL_KEY";
