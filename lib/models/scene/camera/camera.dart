import 'dart:core';

import 'package:playx_3d_scene/models/scene/camera/enums/camera_mode.dart';
import 'package:playx_3d_scene/models/scene/camera/enums/fov.dart';
import 'package:playx_3d_scene/models/scene/camera/exposure.dart';
import 'package:playx_3d_scene/models/scene/camera/projection.dart';
import 'package:playx_3d_scene/models/scene/geometry/position.dart';

import 'lens_projection.dart';

class Camera {
  Exposure? exposure;
  Projection? projection;
  LensProjection? lensProjection;
  List<double>? scaling;
  List<double>? shift;

  Mode? _mode;
  PlayxPosition? targetPosition;
  PlayxPosition? upVector;
  double? zoomSpeed;
  //orbit
  PlayxPosition? orbitHomePosition;
  List<double>? orbitSpeed;
  Fov? fovDirection;
  double? fovDegrees;
  double? farPlane;
  //map
  List<double>? mapExtent;
  double? mapMinDistance;
  //freeflight
  PlayxPosition? flightStartPosition;
  List<double>? flightStartOrientation;
  double? flightMaxMoveSpeed;
  num? flightSpeedSteps;
  double? flightMoveDamping;
  List<double>? groundPlane;

  Camera.orbit({
    this.exposure,
    this.projection,
    this.lensProjection,
    this.scaling,
    this.shift,
    this.targetPosition,
    this.upVector,
    this.zoomSpeed,
    this.groundPlane,
    this.orbitHomePosition,
    this.orbitSpeed,
    this.fovDirection,
    this.fovDegrees,
    this.farPlane,
  }) {
    _mode = Mode.orbit;
  }

  Camera.map({
    this.exposure,
    this.projection,
    this.lensProjection,
    this.scaling,
    this.shift,
    this.targetPosition,
    this.upVector,
    this.zoomSpeed,
    this.groundPlane,
    this.mapExtent,
    this.mapMinDistance,
  }) {
    _mode = Mode.map;
  }

  Camera.freeFlight({
    this.exposure,
    this.projection,
    this.lensProjection,
    this.scaling,
    this.shift,
    this.targetPosition,
    this.upVector,
    this.zoomSpeed,
    this.groundPlane,
    this.flightStartPosition,
    this.flightStartOrientation,
    this.flightMaxMoveSpeed,
    this.flightSpeedSteps,
    this.flightMoveDamping,
  }) {
    _mode = Mode.freeFlight;
  }

  Map<String, dynamic> toJson() {
    return {
      "exposure": exposure?.toJson(),
      "projection": projection?.toJson(),
      "lensProjection": lensProjection?.toJson(),
      "scaling": scaling,
      "shift": shift,
      "mode": _mode?.toName(),
      "targetPosition": targetPosition?.toJson(),
      "upVector": upVector?.toJson(),
      "zoomSpeed": zoomSpeed,
      "orbitHomePosition": orbitHomePosition?.toJson(),
      "orbitSpeed": orbitSpeed,
      "fovDirection": fovDirection?.toName(),
      "fovDegrees": fovDegrees,
      "farPlane": farPlane,
      "mapExtent": mapExtent,
      "mapMinDistance": mapMinDistance,
      "flightStartPosition": flightStartPosition?.toJson(),
      "flightStartOrientation": flightStartOrientation,
      "flightMaxMoveSpeed": flightMaxMoveSpeed,
      "flightSpeedSteps": flightSpeedSteps,
      "flightMoveDamping": flightMoveDamping,
      "groundPlane": groundPlane,
    };
  }
}
