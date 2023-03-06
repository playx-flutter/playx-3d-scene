import 'dart:async';
import 'dart:core';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/models/model/model.dart';
import 'package:playx_3d_scene/models/model_state.dart';
import 'package:playx_3d_scene/models/scene/scene.dart';

typedef Playx3dSceneCreatedCallback = void Function(
    Playx3dSceneController controller);

typedef Playx3dModelStateCallback = void Function(ModelState state);

const String _channelName = "io.sourcya.playx.3d.scene.channel";
const String _viewType = "${_channelName}_3d_scene";
const String _modelStateChannelName =
    "io.sourcya.playx.3d.scene.model_state_channel";

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

  /// onModelStateChanged callback provides current state of the model
  /// whether is none, loading, loaded, fallback loaded ,error.
  final Playx3dModelStateCallback? onModelStateChanged;

  const Playx3dScene(
      {super.key,
      this.model,
      this.scene,
      this.onCreated,
      this.onModelStateChanged});

  @override
  State<StatefulWidget> createState() {
    return PlayxModelViewerState();
  }
}

class PlayxModelViewerState extends State<Playx3dScene> {
  final Map<String, dynamic> creationParams = <String, dynamic>{};

  late EventChannel _modelLoadingChannel;
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

  void setUpModelState(Playx3dSceneController controller) {
    if (widget.onModelStateChanged != null) {
      _modelLoadingSubscription = getModelState().listen((state) {
        if (widget.onModelStateChanged != null) {
          widget.onModelStateChanged!(state);
        }
      });
    }
  }

  void _onPlatformViewCreated(int id) {
    final controller = Playx3dSceneController(id: id);

    if (widget.onCreated != null) {
      widget.onCreated!(controller);
    }
    _modelLoadingChannel = EventChannel('${_modelStateChannelName}_$id');
    setUpModelState(controller);
  }

  @override
  void dispose() {
    _modelLoadingSubscription?.cancel();
    super.dispose();
  }

  Stream<ModelState> getModelState() {
    return _modelLoadingChannel.receiveBroadcastStream().map((state) {
      final currentState = state as String?;
      return ModelState.from(currentState);
    });
  }
}
