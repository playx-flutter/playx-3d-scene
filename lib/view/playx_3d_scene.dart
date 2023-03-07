import 'dart:async';
import 'dart:core';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/models/model/model.dart';
import 'package:playx_3d_scene/models/scene/scene.dart';
import 'package:playx_3d_scene/models/state/model_state.dart';
import 'package:playx_3d_scene/models/state/scene_state.dart';

typedef Playx3dSceneCreatedCallback = void Function(
    Playx3dSceneController controller);

typedef Playx3dModelStateCallback = void Function(ModelState state);

typedef Playx3dOnEachRenderCallback = void Function(num? frameTimeNanos);

typedef Playx3dSceneStateCallback = void Function(SceneState state);

const String _channelName = "io.sourcya.playx.3d.scene.channel";
const String _viewType = "${_channelName}_3d_scene";
const String _modelStateChannelName =
    "io.sourcya.playx.3d.scene.model_state_channel";
const String _rendererChannelName =
    "io.sourcya.playx.3d.scene.renderer_channel";

const String _sceneStateChannelName = "io.sourcya.playx.3d.scene.scene_state";

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

  /// onSceneStateChanged callback provides current state of the scene;
  /// whether is none, loading, loaded ,error.
  final Playx3dSceneStateCallback? onSceneStateChanged;

  /// onEachRenderCallback callback that is called on each frame render.
  /// it also provides last frame render time in nanoseconds.
  final Playx3dOnEachRenderCallback? onEachRender;

  const Playx3dScene(
      {super.key,
      this.model,
      this.scene,
      this.onCreated,
      this.onModelStateChanged,
      this.onEachRender,
      this.onSceneStateChanged});

  @override
  State<StatefulWidget> createState() {
    return PlayxModelViewerState();
  }
}

class PlayxModelViewerState extends State<Playx3dScene> {
  final Map<String, dynamic> creationParams = <String, dynamic>{};

  late EventChannel _modelLoadingChannel;
  StreamSubscription? _modelLoadingSubscription;
  late EventChannel _rendererChannel;
  StreamSubscription? _rendererSubscription;
  late EventChannel _sceneStateChannel;
  StreamSubscription? _sceneStateSubscription;

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

  void setUpSceneState(Playx3dSceneController controller) {
    if (widget.onSceneStateChanged != null) {
      _sceneStateSubscription = getSceneState().listen((state) {
        if (widget.onSceneStateChanged != null) {
          widget.onSceneStateChanged!(state);
        }
      });
    }
  }

  void setUpOnEachRenderCallback(Playx3dSceneController controller) {
    if (widget.onEachRender != null) {
      _rendererSubscription = getOnEachRender().listen((time) {
        if (widget.onEachRender != null) {
          widget.onEachRender!(time);
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
    _sceneStateChannel = EventChannel('${_sceneStateChannelName}_$id');
    _rendererChannel = EventChannel('${_rendererChannelName}_$id');
    setUpModelState(controller);
    setUpSceneState(controller);
    setUpOnEachRenderCallback(controller);
  }

  @override
  void dispose() {
    _modelLoadingSubscription?.cancel();
    _rendererSubscription?.cancel();
    _sceneStateSubscription?.cancel();
    super.dispose();
  }

  Stream<ModelState> getModelState() {
    return _modelLoadingChannel.receiveBroadcastStream().map((state) {
      final currentState = state as String?;
      return ModelState.from(currentState);
    });
  }

  Stream<num?> getOnEachRender() {
    return _rendererChannel.receiveBroadcastStream().map((time) {
      return time as num?;
    });
  }

  Stream<SceneState> getSceneState() {
    return _sceneStateChannel.receiveBroadcastStream().map((state) {
      final currentState = state as String?;
      return SceneState.from(currentState);
    });
  }
}
