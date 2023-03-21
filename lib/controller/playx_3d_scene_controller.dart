import 'package:flutter/services.dart';
import 'package:playx_3d_scene/models/scene/camera/camera.dart';
import 'package:playx_3d_scene/models/scene/camera/exposure.dart';
import 'package:playx_3d_scene/models/scene/camera/lens_projection.dart';
import 'package:playx_3d_scene/models/scene/camera/projection.dart';
import 'package:playx_3d_scene/models/scene/geometry/position.dart';
import 'package:playx_3d_scene/models/scene/ground.dart';
import 'package:playx_3d_scene/models/scene/indirect_light/indirect_light.dart';
import 'package:playx_3d_scene/models/scene/light/light.dart';
import 'package:playx_3d_scene/models/scene/material/material.dart';
import 'package:playx_3d_scene/models/shapes/shape.dart';
import 'package:playx_3d_scene/models/state/model_state.dart';
import 'package:playx_3d_scene/utils/result.dart';
import 'package:playx_3d_scene/utils/utils.dart';

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
  Future<Result<int>> changeAnimationByIndex(int? index) {
    final data = _channel.invokeMethod<int>(
      _changeAnimationByIndex,
      {_changeAnimationByIndexKey: index},
    );
    return handleError(data);
  }

  /// Changes the current animation by animation name.
  /// it takes an String? animation name as an argument.
  /// and returns the new animation index.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<int>> changeAnimationByName(String? animationName) {
    final data = _channel.invokeMethod<int>(
      _changeAnimationByName,
      {_changeAnimationByNameKey: animationName},
    );
    return handleError(data);
  }

  /// Get current model animation names.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<List<String>>> getAnimationNames() {
    final data = _channel.invokeMethod<List<Object?>>(
      _getAnimationNames,
      {},
    ).then((value) {
      return value?.map((e) => e as String).toList();
    });
    return handleError(data);
  }

  /// Get current model animation count.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<int>> getAnimationCount() {
    final data = _channel.invokeMethod<int>(
      _getAnimationCount,
      {},
    );
    return handleError(data);
  }

  /// Get current animation index.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<int>> getCurrentAnimationIndex() {
    final data = _channel.invokeMethod<int>(
      _getCurrentAnimationIndex,
      {},
    );
    return handleError(data);
  }

  /// Get Animation name by given index.
  /// it takes an Int? index as an argument.
  /// and returns the animation name.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> getAnimationNameByIndex(int? index) {
    final data = _channel.invokeMethod<String>(
      _getAnimationNameByIndex,
      {_getAnimationNameByIndexKey: index},
    );
    return handleError(data);
  }

  //skybox

  /// change environment by given asset path.
  /// it takes an String? asset path as an argument.
  /// should be provided with the KTX skybox file.
  /// so it can update the environment skybox with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeSkyboxByKtxAsset(String? path) {
    final data = _channel.invokeMethod<String>(
      _changeSkyboxByAsset,
      {_changeSkyboxByAssetKey: path},
    );
    return handleError(data);
  }

  /// change environment by given asset path.
  /// it takes an String? asset path as an argument.
  /// should be provided with the KTX skybox file.
  /// so it can update the environment skybox with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeSkyboxByKtxUrl(String? url) {
    final data = _channel.invokeMethod<String>(
      _changeSkyboxByUrl,
      {_changeSkyboxByUrlKey: url},
    );
    return handleError(data);
  }

  /// change skybox by given asset path.
  /// it takes an String? asset path as an argument.
  /// should be provided with the HDR file.
  /// so it can update the environment skybox with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeSkyboxByHdrAsset(String? path) {
    final data = _channel.invokeMethod<String>(
      _changeSkyboxByHdrAsset,
      {_changeSkyboxByHdrAssetKey: path},
    );
    return handleError(data);
  }

  /// change skybox by given asset path.
  /// it takes an String? asset path as an argument.
  /// should be provided with the the HDR file.
  /// so it can update the environment skybox with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeSkyboxByHdrUrl(String? url) {
    final data = _channel.invokeMethod<String>(
      _changeSkyboxByHdrUrl,
      {_changeSkyboxByHdrUrlKey: url},
    );
    return handleError(data);
  }

  /// change environment by given color.
  /// it takes an Color?  as an argument.
  /// and updates the environment skybox color
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeSkyboxColor(Color? color) {
    final environmentColor = color?.toHex;
    final data = _channel.invokeMethod<String>(
      _changeSkyboxColor,
      {_changeSkyboxColorKey: environmentColor},
    );
    return handleError(data);
  }

  /// change environment to be transparent.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeToTransparentSkybox() {
    final data = _channel.invokeMethod<String>(
      _changeToTransparentSkybox,
      {},
    );
    return handleError(data);
  }

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
  Future<Result<String>> changeIndirectLightByKtxAsset(
      {required String? path, double? intensity}) {
    final data = _channel.invokeMethod<String>(
      _changeLightByKtxAsset,
      {
        _changeLightByKtxAssetKey: path,
        _changeLightByKtxAssetIntensityKey: intensity
      },
    );
    return handleError(data);
  }

  /// change scene indirect light by given ktx file url .
  /// it takes an String? asset path as an argument.
  /// and can take light intensity as an argument.
  /// should be provided with the KTX image based lighting file.
  /// so it can update the scene light with it.
  /// if intensity is provide, it will update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeIndirectLightByKtxUrl(
      {required String? url, double? intensity}) {
    final data = _channel.invokeMethod<String>(
      _changeLightByKtxUrl,
      {
        _changeLightByKtxUrlKey: url,
        _changeLightByKtxUrlIntensityKey: intensity
      },
    );
    return handleError(data);
  }

  /// change scene indirect light by given HDR asset path.
  /// it takes an String? asset path as an argument.
  /// and can take light intensity as an argument.
  /// should be provided with the HDR image based lighting file.
  /// so it can update the scene light with it.
  /// if intensity is provide, it will update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeIndirectLightByHdrAsset(
      {required String? path, double? intensity}) {
    final data = _channel.invokeMethod<String>(
      _changeLightByHdrAsset,
      {
        _changeLightByHdrAssetKey: path,
        _changeLightByHdrAssetIntensityKey: intensity
      },
    );
    return handleError(data);
  }

  /// change scene indirect light by given Hdr file url .
  /// it takes an String? asset path as an argument.
  /// and can take light intensity as an argument.
  /// should be provided with the HDR image based lighting file.
  /// so it can update the scene light with it.
  /// if intensity is provide, it will update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeIndirectLightByHdrUrl(
      {required String? url, double? intensity}) {
    final data = _channel.invokeMethod<String>(
      _changeLightByHdrUrl,
      {
        _changeLightByHdrUrlKey: url,
        _changeLightByHdrUrlIntensityKey: intensity
      },
    );
    return handleError(data);
  }

  /// change scene indirect light by given intensity.
  /// it takes light intensity as an argument.
  /// and update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeIndirectLightByDefaultIndirectLight(
      IndirectLight indirectLight) {
    final data = _channel.invokeMethod<String>(
      _changeLightByIndirectLight,
      {_changeLightByIndirectLightKey: indirectLight.toJson()},
    );
    return handleError(data);
  }

  /// change scene indirect light to the default intensity which is 40_000.0.
  /// with radiance bands of 1.0, radiance sh of [1,1,1]
  /// and irradiance bands of 1.0, irradiance sh of [1,1,1]
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeToDefaultIndirectLight() {
    final data = _channel.invokeMethod<String>(
      _changeToDefaultIndirectLight,
      {},
    );
    return handleError(data);
  }

  /// change scene light by given intensity.
  /// it takes light light as an argument.
  /// and update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeSceneLight(Light light) {
    final data = _channel.invokeMethod<String>(
      _changeLight,
      {_changeLightKey: light.toJson()},
    );
    return handleError(data);
  }

  /// change scene indirect light to the default intensity which is 100_000.0.
  /// with color temperature bands of 6_500.0,
  /// and direction of [0.0, -1.0f, 0.0f],
  /// and cast shadows true.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeToDefaultLight() {
    final data = _channel.invokeMethod<String>(
      _changeToDefaultLight,
      {},
    );
    return handleError(data);
  }

  //load model

  /// Load glb model from assets.
  /// it takes asset path as an argument.
  /// and update the current model with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> loadGlbModelFromAssets(String? path) {
    final data = _channel.invokeMethod<String>(
      _loadGlbModelFromAssets,
      {_loadGlbModelFromAssetsPathKey: path},
    );
    return handleError(data);
  }

  /// Load glb model from url.
  /// it takes url as an argument.
  /// and update the current model with it.
  /// and returns a message whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result> loadGlbModelFromUrl(String? url) {
    final data = _channel.invokeMethod<String>(
      _loadGlbModelFromUrl,
      {_loadGlbModelFromUrlKey: url},
    );
    return handleError(data);
  }

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
  Future<Result<String>> loadGltfModelFromAssets(String? path,
      {String? imagePathPrefix, String? imagePathPostfix}) {
    final data = _channel.invokeMethod<String>(
      _loadGltfModelFromAssets,
      {
        _loadGltfModelFromAssetsPathKey: path,
        _loadGltfModelFromAssetsPrefixPathKey: imagePathPrefix,
        _loadGltfModelFromAssetsPostfixPathKey: imagePathPostfix
      },
    );
    return handleError(data);
  }

  /// change model scale.
  /// it takes scale as an argument.
  /// and update the current model scale.
  /// and returns a message whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeModelScale(double? scale) {
    final data = _channel.invokeMethod<String>(
      _changeModelScale,
      {_changeModelScaleKey: scale},
    );
    return handleError(data);
  }

  /// change model center position.
  /// it takes list of double of [x,y,z] coordinates as an argument.
  /// and update the current model center position.
  /// and returns a message whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<Result<String>> changeModelCenterPosition(
      PlayxPosition centerPosition) {
    final data = _channel.invokeMethod<String>(
      _changeModelPosition,
      {_changeModelPositionKey: centerPosition.toJson()},
    );
    return handleError(data);
  }

  /// Get current model state.
  Future<Result<ModelState>> getCurrentModelState() async {
    try {
      final state = await _channel.invokeMethod<String>(
        _getCurrentModelState,
        {},
      );
      return Result.success(ModelState.from(state));
    } on PlatformException catch (err) {
      return Result.error(err.message);
    } catch (err) {
      return Result.error("Something went wrong");
    }
  }

  Future<Result<String>> updateCamera(Camera? camera) {
    final data = _channel.invokeMethod<String>(
      _updateCamera,
      {_updateCameraKey: camera?.toJson()},
    );
    return handleError(data);
  }

  Future<Result<String>> updateExposure(Exposure? exposure) {
    final data = _channel.invokeMethod<String>(
      _updateExposure,
      {_updateExposureKey: exposure?.toJson()},
    );
    return handleError(data);
  }

  Future<Result<String>> updateProjection(Projection? projection) {
    final data = _channel.invokeMethod<String>(
      _updateProjection,
      {_updateProjectionKey: projection?.toJson()},
    );
    return handleError(data);
  }

  Future<Result<String>> updateLensProjection(LensProjection? lensProjection) {
    final data = _channel.invokeMethod<String>(
      _updateLensProjection,
      {_updateLensProjectionKey: lensProjection?.toJson()},
    );
    return handleError(data);
  }

  Future<Result<String>> updateCameraShift(List<double>? shift) {
    final data = _channel.invokeMethod<String>(
      _updateCameraShift,
      {_updateCameraShiftKey: shift},
    );
    return handleError(data);
  }

  Future<Result<String>> updateCameraScaling(List<double>? scaling) {
    final data = _channel.invokeMethod<String>(
      _updateCameraScaling,
      {_updateCameraScalingKey: scaling},
    );
    return handleError(data);
  }

  Future<Result<String>> setDefaultCamera() {
    final data = _channel.invokeMethod<String>(
      _setDefaultCamera,
      {},
    );
    return handleError(data);
  }

  Future<Result<String>> lookAtDefaultCameraPosition() {
    final data = _channel.invokeMethod<String>(
      _lookAtDefaultPosition,
      {},
    );
    return handleError(data);
  }

  Future<Result<List<double>>> getCameraLookAtPositions() async {
    try {
      final positions = await _channel.invokeMethod<List<Object?>?>(
        _getLookAt,
        {},
      );
      final result = positions?.map((e) => (e as double)).toList();
      return Result.success(result);
    } on PlatformException catch (err) {
      return Result.error(err.message);
    } catch (err) {
      return Result.error("Something went wrong");
    }
  }

  Future<Result> lookAtCameraPosition({
    List<double>? eyePos,
    List<double>? targetPos,
    List<double>? upwardPos,
  }) {
    final data = _channel.invokeMethod<String>(
      _lookAtPosition,
      {
        _eyeArrayKey: eyePos,
        _targetArrayKey: targetPos,
        _upwardArrayKey: upwardPos
      },
    );
    return handleError(data);
  }

  Future<Result<String>> scrollCameraTo({
    num? x,
    num? y,
    double? scrollDelta,
  }) {
    final data = _channel.invokeMethod<String>(
      _cameraScroll,
      {
        _cameraScrollXKey: x,
        _cameraScrollYKey: y,
        _cameraScrollDeltaKey: scrollDelta
      },
    );
    return handleError(data);
  }

  Future<Result<String>> beginCameraGrab({
    num? x,
    num? y,
    bool? strafe,
  }) {
    final data = _channel.invokeMethod<String>(
      _cameraGrabBegin,
      {
        _cameraGrabBeginXKey: x,
        _cameraGrabBeginYKey: y,
        _cameraGrabBeginStrafeKey: strafe
      },
    );
    return handleError(data);
  }

  Future<Result<String>> updateCameraGrab({
    num? x,
    num? y,
  }) {
    final data = _channel.invokeMethod<String>(
      _cameraGrabUpdate,
      {
        _cameraGrabUpdateXKey: x,
        _cameraGrabUpdateYKey: y,
      },
    );
    return handleError(data);
  }

  Future<Result<String>> endCameraGrab() {
    final data = _channel.invokeMethod<String>(
      _cameraGrabEnd,
      {},
    );
    return handleError(data);
  }

  Future<Result<String>> getCameraRayCast({
    num? x,
    num? y,
  }) {
    final data = _channel.invokeMethod<String>(
      _cameraRayCast,
      {
        _cameraRayCastXKey: x,
        _cameraRayCastYKey: y,
      },
    );
    return handleError(data);
  }

  Future<Result<String>> updateGround(Ground? ground) {
    final data = _channel.invokeMethod<String>(
      _updateGround,
      {_updateGroundKey: ground?.toJson()},
    );
    return handleError(data);
  }

  Future<Result<String>> updateGroundMaterial(PlayxMaterial material) {
    final data = _channel.invokeMethod<String>(
      _updateGroundMaterial,
      {_updateGroundMaterialKey: material.toJson()},
    );
    return handleError(data);
  }

  Future<Result<String>> addShape(Shape shape) {
    final data = _channel.invokeMethod<String>(
      _addShape,
      {_addShapeKey: shape.toJson()},
    );
    return handleError(data);
  }

  Future<Result<String>> removeShape(int id) {
    final data = _channel.invokeMethod<String>(
      _removeShape,
      {_removeShapeKey: id},
    );
    return handleError(data);
  }

  Future<Result<String>> updateShape(int id, Shape shape) {
    final data = _channel.invokeMethod<String>(
      _updateShape,
      {_updateShapeKey: shape.toJson(), _updateShapeIdKey: id},
    );
    return handleError(data);
  }

  Future<Result<List<int>>> getCurrentShapesIds() {
    final data = _channel.invokeMethod<List<Object?>?>(
      _getCurrentCreatedShapesIds,
      {},
    ).then((value) {
      final List<int> ids = [];
      value?.forEach((element) {
        final id = element as int?;
        if (id != null) {
          ids.add(id);
        }
      });
      return ids;
    });
    return handleError(data);
  }
}

Future<Result<T>> handleError<T>(Future<T?> data) async {
  try {
    final result = await data;
    return Result.success(result);
  } on PlatformException catch (err) {
    return Result.error(err.message);
  } catch (err) {
    return Result.error("Something went wrong");
  }
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

const String _addShape = "ADD_SHAPE";
const String _addShapeKey = "ADD_SHAPE_KEY";
const String _removeShape = "REMOVE_SHAPE";
const String _removeShapeKey = "REMOVE_SHAPE_KEY";
const String _updateShape = "UPDATE_SHAPE";
const String _updateShapeKey = "UPDATE_SHAPE_KEY";
const String _updateShapeIdKey = "UPDATE_SHAPE_ID_KEY";
const String _getCurrentCreatedShapesIds = "CREATED_SHAPES_IDS";
