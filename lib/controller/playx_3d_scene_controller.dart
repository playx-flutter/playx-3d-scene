import 'package:flutter/services.dart';

const String _changeAnimationByIndex = "CHANGE_ANIMATION_BY_INDEX";
const String _changeAnimationByIndexKey = "CHANGE_ANIMATION_BY_INDEX_KEY";

const String _changeAnimationByName = "CHANGE_ANIMATION_BY_NAME";
const String _changeAnimationByNameKey = "CHANGE_ANIMATION_BY_NAME_KEY";
const String _getAnimationNames = "GET_ANIMATION_NAMES";

const String _getAnimationNameByIndex = "GET_ANIMATION_NAME_BY_INDEX";
const String _getAnimationNameByIndexKey = "GET_ANIMATION_NAME_BY_INDEX_KEY";

const String _getAnimationCount = "GET_ANIMATION_COUNT";

const String _getCurrentAnimationIndex = "GET_CURRENT_ANIMATION_INDEX";

const String _changeEnvironmentByAsset = "CHANGE_ENVIRONMENT_BY_ASSET";
const String _changeEnvironmentByAssetKey = "CHANGE_ENVIRONMENT_BY_ASSET_KEY";

const String _changeEnvironmentColor = "CHANGE_ENVIRONMENT_COLOR";
const String _changeEnvironmentColorKey = "CHANGE_ENVIRONMENT_COLOR_KEY";

const String _changeToTransparentEnvironment =
    "CHANGE_TO_TRANSPARENT_ENVIRONMENT";

const String _changeLightByAsset = "CHANGE_LIGHT_BY_ASSET";
const String _changeLightByAssetKey = "CHANGE_LIGHT_BY_ASSET_KEY";
const String _changeLightByAssetIntensityKey =
    "CHANGE_LIGHT_BY_ASSET_INTENSITY_KEY";

const String _changeLightByIntensity = "CHANGE_LIGHT_BY_INTENSITY";
const String _changeLightByIntensityKey = "CHANGE_LIGHT_BY_INTENSITY_KEY";
const String _changeToDefaultLightIntensity =
    "CHANGE_TO_DEFAULT_LIGHT_INTENSITY";

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

class Playx3dSceneController {
  int id;
  late MethodChannel _channel;
  late EventChannel _modelLoadingChannel;

  static const String channelName = "io.sourcya.playx.3d.scene.channel";
  static const String modelLoadingChannelName =
      "io.sourcya.playx.3d.scene.model_loading_channel";

  Playx3dSceneController({required this.id}) {
    _channel = MethodChannel('${channelName}_$id');
    _modelLoadingChannel = EventChannel('${modelLoadingChannelName}_$id');
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

  //environment

  /// change environment by given asset path.
  /// it takes an String? asset path as an argument.
  /// should be provided with the KTX skybox file.
  /// so it can update the environment skybox with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeEnvironmentByAsset(String? path) =>
      _channel.invokeMethod<String>(
        _changeEnvironmentByAsset,
        {_changeEnvironmentByAssetKey: path},
      );

  /// change environment by given color.
  /// it takes an Color?  as an argument.
  /// and updates the environment skybox color
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeEnvironmentColor(Color? color) {
    final environmentColor = color?.value;
    return _channel.invokeMethod<String>(
      _changeEnvironmentColor,
      {_changeEnvironmentColorKey: environmentColor},
    );
  }

  /// change environment to be transparent.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeToTransparentEnvironment() =>
      _channel.invokeMethod<String>(
        _changeToTransparentEnvironment,
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
  Future<String?> changeLightByAsset(
          {required String? path, double? intensity}) =>
      _channel.invokeMethod<String>(
        _changeLightByAsset,
        {
          _changeLightByAssetKey: path,
          _changeLightByAssetIntensityKey: intensity
        },
      );

  /// change scene indirect light by given intensity.
  /// it takes light intensity as an argument.
  /// and update the scene light intensity with it.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeLightByIntensity(double? intensity) =>
      _channel.invokeMethod<String>(
        _changeLightByIntensity,
        {_changeLightByIntensityKey: intensity},
      );

  /// change scene indirect light to the default intensity which is 40_000.0.
  /// and returns whether it succeeded or not.
  /// it can throw an exception if something went wrong.
  /// you can catch the platform exception to get the error message.
  Future<String?> changeToDefaultLightIntensity() =>
      _channel.invokeMethod<String>(
        _changeToDefaultLightIntensity,
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

  Stream<bool> getModelLoadingState() {
    return _modelLoadingChannel.receiveBroadcastStream().map((isLoading) {
      return isLoading as bool;
    });
  }
}
