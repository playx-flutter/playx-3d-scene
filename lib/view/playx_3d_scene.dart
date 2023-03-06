import 'dart:async';
import 'dart:core';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/models/model/model.dart';
import 'package:playx_3d_scene/models/scene/scene.dart';

typedef Playx3dSceneCreatedCallback = void Function(
    Playx3dSceneController controller);

typedef Playx3dModelLoadingCallback = void Function(bool isLoading);

const String _viewType = "${Playx3dSceneController.channelName}_3d_scene";

class Playx3dScene extends StatefulWidget {
  /// Model to be rendered.
  /// provide details about the model to be rendered.
  /// like asset path, url, animation, etc.
  final Model? model;

  /// Scene to be rendered.
  /// provide details about the scene to be rendered.
  /// like skybox, lightening, camera, etc.
  final Scene? scene;

  /// onCreated callback provides PlayX Model viewer controller.
  /// when the viewer is created.
  /// provides utility methods to update the viewer.
  /// you can use it to change the animation, environment, lightening, etc.
  final Playx3dSceneCreatedCallback? onCreated;

  /// onModelLoadingStateChanged callback provides bool whether the model is loading or not.
  final Playx3dModelLoadingCallback? onModelLoadingStateChanged;

  const Playx3dScene(
      {super.key,
      this.model,
      this.scene,
      this.onCreated,
      this.onModelLoadingStateChanged});

  @override
  State<StatefulWidget> createState() {
    return PlayxModelViewerState();
  }
}

class PlayxModelViewerState extends State<Playx3dScene> {
  final Map<String, dynamic> creationParams = <String, dynamic>{};
  StreamSubscription? _modelLoadingSubscription;

  PlayxModelViewerState();

  @override
  void initState() {
    setupCreationParams();

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: _viewType,
        creationParams: creationParams,
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
    return Text('$defaultTargetPlatform is not yet supported by the plugin');
  }

  void setupCreationParams() {
    final model = widget.model?.toJson();
    final scene = widget.scene?.toJson();
    creationParams["model"] = model;
    creationParams["scene"] = scene;
  }

  void setUpModelLoading(Playx3dSceneController controller) {
    if (widget.onModelLoadingStateChanged != null) {
      _modelLoadingSubscription =
          controller.getModelLoadingState().listen((isLoading) {
        if (widget.onModelLoadingStateChanged != null) {
          widget.onModelLoadingStateChanged!(isLoading);
        }
      });
    }
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onCreated != null) {
      final controller = Playx3dSceneController(id: id);
      widget.onCreated!(controller);
      setUpModelLoading(controller);
    }
  }

  @override
  void dispose() {
    _modelLoadingSubscription?.cancel();
    super.dispose();
  }
}
