import 'dart:core';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:playx_model_viewer/controller/playx_model_viewer_controller.dart';

typedef PlayXModelViewerCreatedCallback = void Function(
    PlayXModelViewerController controller);

class PlayXModelViewer extends StatefulWidget {
  /// glb asset path to be loaded from assets.
  final String? glbAssetPath;

  /// glb model url to be loaded from url.
  final String? glbUrl;

  /// gltf asset path to be loaded from assets.
  final String? gltfAssetPath;

  /// prefix for gltf image assets.
  /// if the images path that in the gltf file different from the flutter asset path,
  /// you can add prefix to the images path to be before the image.
  /// LIKE if in the gltf file, the image path is textures/texture.png
  /// and in assets the image path is assets/models/textures/texture.png
  /// you will need to add prefix to be 'assets/models/'.
  final String gltfImagePathPrefix;

  /// postfix path for gltf image assets.
  /// if the images path that in the gltf file different from the flutter asset path,
  /// you can add postfix to the images path to be after the image.
  /// LIKE if in the gltf file, the image path is assets/textures/texture
  /// and in assets the image path is assets/textures/texture.png
  /// you will need to add prefix to be '.png'.
  final String gltfImagePathPostfix;

  /// light asset path used to load KTX FILE from assets.
  /// used to change indirect lighting from Image-Based Light.
  final String? lightAssetPath;

  /// indirect light intensity.
  /// can be used with light asset path.
  /// or create default light with certain intensity.
  final double? lightIntensity;

  /// environment asset path used to load KTX FILE from assets.
  /// changes scene skybox from images converted to KTX FILE.
  /// Filament provides an offline tool called cmgen
  /// that can consume an image
  /// and produce Light and skybox ktx files in one fell swoop.
  final String? environmentAssetPath;

  /// Environment Color.
  /// Changes the background color for the scene.
  /// if not provided and environment asset path is not provided,
  /// A Transparent color will be used.
  final Color? environmentColor;

  /// Animation Index of the Animation to be used.
  final int? animationIndex;

  /// Animation Name of the Animation to be used.
  final String? animationName;

  /// auto play : decides whether to play the animation automatically or not
  /// default is false.
  final bool autoPlay;

  /// onCreated callback provides PlayX Model viewer controller.
  /// when the viewer is created.
  /// provides utility methods to update the viewer.
  /// you can use it to change the animation, environment, lightening, etc.
  final PlayXModelViewerCreatedCallback? onCreated;

  const PlayXModelViewer({
    super.key,
    this.glbAssetPath,
    this.glbUrl,
    this.gltfAssetPath,
    this.gltfImagePathPrefix = "",
    this.gltfImagePathPostfix = "",
    this.lightAssetPath,
    this.lightIntensity,
    this.environmentAssetPath,
    this.environmentColor,
    this.animationIndex,
    this.animationName,
    this.autoPlay = false,
    this.onCreated,
  });

  @override
  State<StatefulWidget> createState() {
    return PlayxModelViewerState();
  }
}

class PlayxModelViewerState extends State<PlayXModelViewer> {
  static const String viewType =
      "${PlayXModelViewerController.channelName}_model_view";
  static const String glbAssetPathKey = "GLB_ASSET_PATH_KEY";
  static const String glbUrlKey = "GLB_URL_KEY";
  static const String gltfAssetPathKey = "GLTF_ASSET_PATH_KEY";
  static const String gltfImagePathPrefixKey = "GLTF_IMAGE_PATH_PREFIX_KEY";
  static const String gltfImagePathPostfixKey = "GLTF_IMAGE_PATH_POSTFIX_KEY";
  static const String lightAssetPathKey = "LIGHT_ASSET_PATH_KEY";
  static const String lightIntensityKey = "LIGHT_INTENSITY_KEY";
  static const String environmentAssetPathKey = "ENVIRONMENT_ASSET_PATH_KEY";
  static const String environmentColorKey = "ENVIRONMENT_COLOR_KEY";
  static const String animationIndexKey = "ANIMATION_INDEX_KEY";
  static const String animationNameKey = "ANIMATION_NAME_KEY";
  static const String autoPlayKey = "AUTO_PLAY_KEY";

  final Map<String, dynamic> creationParams = <String, dynamic>{};

  PlayxModelViewerState();

  @override
  void initState() {
    creationParams[glbAssetPathKey] = widget.glbAssetPath;
    creationParams[glbUrlKey] = widget.glbUrl;
    creationParams[gltfAssetPathKey] = widget.gltfAssetPath;
    creationParams[gltfImagePathPrefixKey] = widget.gltfImagePathPrefix;
    creationParams[gltfImagePathPostfixKey] = widget.gltfImagePathPostfix;
    creationParams[lightAssetPathKey] = widget.lightAssetPath;
    creationParams[lightIntensityKey] = widget.lightIntensity;
    creationParams[environmentAssetPathKey] = widget.environmentAssetPath;
    creationParams[environmentColorKey] = widget.environmentColor?.value;
    creationParams[animationIndexKey] = widget.animationIndex;
    creationParams[animationNameKey] = widget.animationName;
    creationParams[autoPlayKey] = widget.autoPlay;

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: viewType,
        creationParams: creationParams,
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
    return Text('$defaultTargetPlatform is not yet supported by the plugin');
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onCreated != null) {
      widget.onCreated!(PlayXModelViewerController(id: id));
    }
  }
}
