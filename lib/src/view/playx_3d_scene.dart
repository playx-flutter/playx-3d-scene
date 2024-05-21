import 'dart:async';
import 'dart:core';

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:playx_3d_scene/src/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/src/models/model/model.dart';
import 'package:playx_3d_scene/src/models/scene/camera/camera.dart';
import 'package:playx_3d_scene/src/models/scene/ground.dart';
import 'package:playx_3d_scene/src/models/scene/indirect_light/indirect_light.dart';
import 'package:playx_3d_scene/src/models/scene/light/light.dart';
import 'package:playx_3d_scene/src/models/scene/scene.dart';
import 'package:playx_3d_scene/src/models/scene/skybox/skybox.dart';
import 'package:playx_3d_scene/src/models/shapes/cube.dart';
import 'package:playx_3d_scene/src/models/shapes/plane.dart';
import 'package:playx_3d_scene/src/models/shapes/shape.dart';
import 'package:playx_3d_scene/src/models/shapes/sphere.dart';
import 'package:playx_3d_scene/src/models/state/model_state.dart';
import 'package:playx_3d_scene/src/models/state/scene_state.dart';
import 'package:playx_3d_scene/src/models/state/shape_state.dart';

typedef Playx3dSceneCreatedCallback = void Function(
    Playx3dSceneController controller);

typedef Playx3dModelStateCallback = void Function(ModelState state);

typedef Playx3dOnEachRenderCallback = void Function(num? frameTimeNanos);

typedef Playx3dSceneStateCallback = void Function(SceneState state);

typedef Playx3dShapeStateCallback = void Function(ShapeState state);

const String _channelName = "io.sourcya.playx.3d.scene.channel";
const String _viewType = "${_channelName}_3d_scene";
const String _modelStateChannelName =
    "io.sourcya.playx.3d.scene.model_state_channel";
const String _rendererChannelName =
    "io.sourcya.playx.3d.scene.renderer_channel";

const String _sceneStateChannelName = "io.sourcya.playx.3d.scene.scene_state";
const String _shapeStateChannelName = "io.sourcya.playx.3d.scene.shape_state";

class Playx3dScene extends StatefulWidget {
  /// Model to be rendered.
  /// provide details about the model to be rendered.
  /// like asset path, url, animation, etc.
  final Model? model;

  /// Scene to be rendered.
  /// provide details about the scene to be rendered.
  /// like skybox, light, camera, etc.
  /// Default scene is a transparent [Skybox] with default [Light] and default [IndirectLight]
  /// with default [Camera] and no [Ground]
  final Scene? scene;

  /// List of shapes to be rendered.
  /// could be plane cube or sphere.
  /// each shape will be rendered with its own position size and material.
  /// See also:
  /// [Shape]
  /// [Cube]
  /// [Sphere]
  /// [Plane]
  final List<Shape>? shapes;

  /// onCreated callback provides an object of [Playx3dSceneController] when the native view is created.
  /// This controller provides utility methods to update the viewer, change the animation environment, lightening, etc.
  /// The onCreated callback is called once when the native view is created and provide unique controller to each widget.
  /// See also:
  /// [Playx3dSceneController]
  final Playx3dSceneCreatedCallback? onCreated;

  /// onModelStateChanged callback provides current state of the model
  /// whether is none, loading, loaded, fallback loaded ,error.
  /// See Also:
  /// [ModelState]
  final Playx3dModelStateCallback? onModelStateChanged;

  /// onSceneStateChanged callback provides current state of the scene;
  /// whether is none, loading, loaded ,error.
  /// See Also:
  /// [SceneState]
  final Playx3dSceneStateCallback? onSceneStateChanged;

  /// onShapeStateChanged callback provides current state of the shape;
  /// whether is none, loading, loaded ,error.
  /// See Also:
  /// [ShapeState]
  final Playx3dShapeStateCallback? onShapeStateChanged;

  /// onEachRenderCallback callback that is called on each frame render.
  /// it also provides last frame render time in nanoseconds.
  final Playx3dOnEachRenderCallback? onEachRender;

  /// Which gestures should be consumed by the view.
  ///
  /// When the view is put inside other view like [ListView],
  /// it might claim gestures that are recognized by any of the recognizers on this list.
  /// as the [ListView] will handle vertical drags gestures.
  ///
  /// To get the [Playx3dScene] to claim the vertical drag gestures we can pass a vertical drag
  /// gesture recognizer factory in [gestureRecognizers] e.g:
  ///
  /// ```dart
  /// GestureDetector(
  ///   onVerticalDragStart: (DragStartDetails details) {},
  ///   child: SizedBox(
  ///     width: 200.0,
  ///     height: 100.0,
  ///     child: Playx3dScene(
  ///       gestureRecognizers: <Factory<OneSequenceGestureRecognizer>>{
  ///         Factory<OneSequenceGestureRecognizer>(
  ///           () => EagerGestureRecognizer(),
  ///         ),
  ///       },
  ///     ),
  ///   ),
  /// )
  /// ```
  ///
  /// When this set is empty, the view will only handle pointer events for gestures that
  /// were not claimed by any other gesture recognizer.
  final Set<Factory<OneSequenceGestureRecognizer>> gestureRecognizers;

  const Playx3dScene(
      {super.key,
      this.model,
      this.scene,
      this.shapes,
      this.onCreated,
      this.onModelStateChanged,
      this.onEachRender,
      this.onSceneStateChanged,
      this.onShapeStateChanged,
      this.gestureRecognizers =
          const <Factory<OneSequenceGestureRecognizer>>{}});

  @override
  State<StatefulWidget> createState() {
    return PlayxModelViewerState();
  }
}

class PlayxModelViewerState extends State<Playx3dScene> {
  final Map<String, dynamic> _creationParams = <String, dynamic>{};
  final Completer<Playx3dSceneController> _controller =
      Completer<Playx3dSceneController>();

  late EventChannel _modelLoadingChannel;
  StreamSubscription? _modelLoadingSubscription;
  late EventChannel _rendererChannel;
  StreamSubscription? _rendererSubscription;
  late EventChannel _sceneStateChannel;
  StreamSubscription? _sceneStateSubscription;
  late EventChannel _shapeStateChannel;
  StreamSubscription? _shapeStateSubscription;

  PlayxModelViewerState();

  @override
  void initState() {
    _setupCreationParams();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: _viewType,
        creationParams: _creationParams,
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: _onPlatformViewCreated,
        gestureRecognizers: widget.gestureRecognizers,
      );
    }
    return Text('$defaultTargetPlatform is not yet supported by the plugin');
  }

  void _setupCreationParams() {
    final model = widget.model?.toJson();
    final scene = widget.scene?.toJson();
    _creationParams["model"] = model;
    _creationParams["scene"] = scene;
    _creationParams["shapes"] =
        widget.shapes?.map((param) => param.toJson()).toList();
  }

  void _setUpModelState(Playx3dSceneController controller) {
    _modelLoadingSubscription?.cancel();
    if (widget.onModelStateChanged != null) {
      _modelLoadingSubscription = _getModelState().listen((state) {
        if (widget.onModelStateChanged != null) {
          widget.onModelStateChanged!(state);
        }
      });
    }
  }

  void _setUpSceneState(Playx3dSceneController controller) {
    _sceneStateSubscription?.cancel();
    if (widget.onSceneStateChanged != null) {
      _sceneStateSubscription = _getSceneState().listen((state) {
        if (widget.onSceneStateChanged != null) {
          widget.onSceneStateChanged!(state);
        }
      });
    }
  }

  void _setUpShapeState(Playx3dSceneController controller) {
    _shapeStateSubscription?.cancel();
    if (widget.onShapeStateChanged != null) {
      _shapeStateSubscription = _getShapeState().listen((state) {
        if (widget.onShapeStateChanged != null) {
          widget.onShapeStateChanged!(state);
        }
      });
    }
  }

  void _setUpOnEachRenderCallback(Playx3dSceneController controller) {
    _rendererSubscription?.cancel();
    if (widget.onEachRender != null) {
      _rendererSubscription = _getOnEachRender().listen((time) {
        if (widget.onEachRender != null) {
          widget.onEachRender!(time);
        }
      });
    }
  }

  void _onPlatformViewCreated(int id) {
    final controller = Playx3dSceneController(id: id);

    _controller.complete(controller);
    if (widget.onCreated != null) {
      widget.onCreated!(controller);
    }
    _modelLoadingChannel = EventChannel('${_modelStateChannelName}_$id');
    _sceneStateChannel = EventChannel('${_sceneStateChannelName}_$id');
    _shapeStateChannel = EventChannel('${_shapeStateChannelName}_$id');
    _rendererChannel = EventChannel('${_rendererChannelName}_$id');

    _setUpModelState(controller);
    _setUpSceneState(controller);
    _setUpShapeState(controller);
    _setUpOnEachRenderCallback(controller);
  }

  Stream<ModelState> _getModelState() {
    return _modelLoadingChannel.receiveBroadcastStream().map((state) {
      final currentState = state as String?;
      return ModelState.from(currentState);
    });
  }

  Stream<num?> _getOnEachRender() {
    return _rendererChannel.receiveBroadcastStream().map((time) {
      return time as num?;
    });
  }

  Stream<SceneState> _getSceneState() {
    return _sceneStateChannel.receiveBroadcastStream().map((state) {
      final currentState = state as String?;
      return SceneState.from(currentState);
    });
  }

  Stream<ShapeState> _getShapeState() {
    return _shapeStateChannel.receiveBroadcastStream().map((state) {
      final currentState = state as String?;
      return ShapeState.from(currentState);
    });
  }

  @override
  void didUpdateWidget(Playx3dScene oldWidget) {
    super.didUpdateWidget(oldWidget);
    _updateWidget(oldWidget);
  }

  void _updateWidget(Playx3dScene? oldWidget) {
    _setupCreationParams();
    if (oldWidget?.model != widget.model ||
        oldWidget?.scene != widget.scene ||
        !listEquals(oldWidget?.shapes, widget.shapes)) {
      _updatePlayxScene();
    }
  }

  Future<void> _updatePlayxScene() async {
    final controller = (await _controller.future);
    await controller.updatePlayx3dScene(
      model: widget.model,
      scene: widget.scene,
      shapes: widget.shapes,
    );
  }

  @override
  void reassemble() {
    super.reassemble();
    // Update scene on hot reload for better debugging
    _updateWidget(null);
  }

  @override
  void dispose() {
    _modelLoadingSubscription?.cancel();
    _rendererSubscription?.cancel();
    _sceneStateSubscription?.cancel();
    _shapeStateSubscription?.cancel();
    super.dispose();
  }
}
