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

class PlayXModelViewerController {
  int id;
  late MethodChannel _channel;
  static const String channelName = "io.sourcya.playx.model_viewer.channel";

  PlayXModelViewerController({required this.id}) {
    _channel = MethodChannel('${channelName}_$id');
  }

  //animation
  Future<int?> changeAnimationByIndex(int? index) => _channel.invokeMethod<int>(
        _changeAnimationByIndex,
        {_changeAnimationByIndexKey: index},
      );

  Future<int?> changeAnimationByName(String? animationName) =>
      _channel.invokeMethod<int>(
        _changeAnimationByName,
        {_changeAnimationByNameKey: animationName},
      );

  Future<List<String?>?> getAnimationNames() =>
      _channel.invokeMethod<List<Object?>>(
        _getAnimationNames,
        {},
      ).then((value) {
        return value?.map((e) => e as String?).toList();
      });

  Future<int?> getAnimationCount() => _channel.invokeMethod<int>(
        _getAnimationCount,
        {},
      );

  Future<int?> getCurrentAnimationIndex() => _channel.invokeMethod<int>(
        _getCurrentAnimationIndex,
        {},
      );

  Future<String?> getAnimationNameByIndex(int? index) =>
      _channel.invokeMethod<String>(
        _getAnimationNameByIndex,
        {_getAnimationNameByIndexKey: index},
      );

  //environment

  Future<String?> changeEnvironmentByAsset(String? path) =>
      _channel.invokeMethod<String>(
        _changeEnvironmentByAsset,
        {_changeEnvironmentByAssetKey: path},
      );

  Future<String?> changeEnvironmentColor(Color? color) {
    final environmentColor = color?.value;
    return _channel.invokeMethod<String>(
      _changeEnvironmentColor,
      {_changeEnvironmentColorKey: environmentColor},
    );
  }

  Future<String?> changeToTransparentEnvironment() =>
      _channel.invokeMethod<String>(
        _changeToTransparentEnvironment,
        {},
      );

  //light
  Future<String?> changeLightByAsset(
          {required String? path, double? intensity}) =>
      _channel.invokeMethod<String>(
        _changeLightByAsset,
        {
          _changeLightByAssetKey: path,
          _changeLightByAssetIntensityKey: intensity
        },
      );

  Future<String?> changeLightByIntensity(double? intensity) =>
      _channel.invokeMethod<String>(
        _changeLightByIntensity,
        {_changeLightByIntensityKey: intensity},
      );

  Future<String?> changeToDefaultLightIntensity() =>
      _channel.invokeMethod<String>(
        _changeToDefaultLightIntensity,
        {},
      );

  //load model

  Future<String?> loadGlbModelFromAssets(String? path) =>
      _channel.invokeMethod<String>(
        _loadGlbModelFromAssets,
        {_loadGlbModelFromAssetsPathKey: path},
      );

  Future<String?> loadGlbModelFromUrl(String? url) =>
      _channel.invokeMethod<String>(
        _loadGlbModelFromUrl,
        {_loadGlbModelFromUrlKey: url},
      );

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
}
