import 'dart:core';

import 'package:playx_3d_scene/models/scene/camera/enums/fov.dart';

import 'enums/projection_type.dart';

class Projection {
  ProjectionType? projection;
  double? left;
  double? right;
  double? bottom;
  double? top;
  double? near;
  double? far;

  double? fovInDegrees;
  double? aspect;
  Fov? fovDirection;

  Projection.fromPlanes(
      {required this.projection,
      required this.left,
      required this.right,
      required this.bottom,
      required this.top,
      this.near,
      this.far});

  Projection.fromFov(
      {required this.fovInDegrees,
      required this.fovDirection,
      this.aspect,
      this.near,
      this.far});

  Map<String, dynamic> toJson() {
    return {
      "projection": projection?.toName(),
      "left": left,
      "right": right,
      "bottom": bottom,
      "top": top,
      "near": near,
      "far": far,
      "fovInDegrees": fovInDegrees,
      "aspect": aspect,
      "direction": fovDirection?.toName(),
    };
  }
}
