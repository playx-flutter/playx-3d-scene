import 'dart:core';

import 'package:playx_3d_scene/src/models/scene/camera/enums/fov.dart';

import 'enums/projection_type.dart';

///An object that controls camera projection matrix.
class Projection {
  ///Denotes the projection type used by this camera.
  ProjectionType? projection;

  ///distance in world units from the camera to the left plane, at the near plane. Precondition: left != right
  double? left;

  ///distance in world units from the camera to the right plane, at the near plane. Precondition: left != right
  double? right;

  ///distance in world units from the camera to the bottom plane, at the near plane. Precondition: bottom != top
  double? bottom;

  ///distance in world units from the camera to the top plane, at the near plane. Precondition: bottom != top
  double? top;

  ///distance in world units from the camera to the near plane.
  /// The near plane's position in view space is z = -near.
  /// Precondition: near > 0 for ProjectionType.PERSPECTIVE or near != far for ProjectionType.ORTHO.
  double? near;

  ///distance in world units from the camera to the far plane.
  /// The far plane's position in view space is z = -far.
  /// Precondition: far > near for ProjectionType.PERSPECTIVE or far != near for ProjectionType.ORTHO.
  double? far;

  /// full field-of-view in degrees. 0 < fovInDegrees < 180
  double? fovInDegrees;

  /// aspect ratio width/height. aspect > 0
  double? aspect;

  ///direction of the field-of-view parameter.
  Fov? fovDirection;

  ///Sets the projection matrix from a frustum defined by six planes.
  Projection.fromPlanes(
      {required this.projection,
      required this.left,
      required this.right,
      required this.bottom,
      required this.top,
      this.near,
      this.far});

  ///Sets the projection matrix from the field-of-view.
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
