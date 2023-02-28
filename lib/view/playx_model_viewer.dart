import 'dart:core';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:playx_model_viewer/controller/playx_model_viewer_controller.dart';

typedef PlayXModelViewerCreatedCallback = void Function(
    PlayXModelViewerController controller);

class PlayXModelViewer extends StatefulWidget {
  // This is used in the platform side to register the view.
// Pass parameters to the platform side.

  final String? glbAssetPath;
  final String? glbUrl;
  final String? gltfAssetPath;
  final String gltfImagePathPrefix;
  final String gltfImagePathPostfix;
  final String? lightAssetPath;
  final double? lightIntensity;
  final String? environmentAssetPath;
  final Color? environmentColor;
  final int? animationIndex;
  final String? animationName;
  final bool autoPlay;
  final PlayXModelViewerCreatedCallback onCreated;

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
    required this.onCreated,
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
      //   return PlatformViewLink(
      //     viewType: viewType,
      //     surfaceFactory: (context, controller) {
      //       return AndroidViewSurface(
      //         controller: controller as AndroidViewController,
      //         gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
      //         hitTestBehavior: PlatformViewHitTestBehavior.opaque,
      //       );
      //     },
      //     onCreatePlatformView: (params) {
      //       return PlatformViewsService.initExpensiveAndroidView(
      //         id: params.id,
      //         viewType: viewType,
      //         layoutDirection: TextDirection.ltr,
      //         creationParams: creationParams,
      //         creationParamsCodec: const StandardMessageCodec(),
      //         onFocus: () {
      //           params.onFocusChanged(true);
      //         },
      //       )
      //         ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
      //         ..create();
      //     },
      //   );
      // }
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
    widget.onCreated(PlayXModelViewerController(id: id));
  }
}
