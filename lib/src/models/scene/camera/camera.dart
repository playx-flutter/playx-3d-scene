import 'dart:core';

import 'package:playx_3d_scene/src/models/scene/camera/enums/camera_mode.dart';
import 'package:playx_3d_scene/src/models/scene/camera/enums/fov.dart';
import 'package:playx_3d_scene/src/models/scene/camera/exposure.dart';
import 'package:playx_3d_scene/src/models/scene/camera/projection.dart';
import 'package:playx_3d_scene/src/models/scene/geometry/position.dart';

import 'lens_projection.dart';

/// An object that controls camera, it describes what mode it operates on, position, exposure and more.
class Camera {
  /// An object that control camera Exposure.
  Exposure? exposure;

  ///An object that controls camera projection matrix.
  Projection? projection;

  ///An object that control camera and set it's projection matrix from the focal length.
  LensProjection? lensProjection;

  /// Sets an additional matrix that scales the projection matrix.
  ///This is useful to adjust the aspect ratio of the camera independent from its projection.
  /// Its sent as List of 2 double elements :
  ///     * xscaling  horizontal scaling to be applied after the projection matrix.
  //      * yscaling  vertical scaling to be applied after the projection matrix.
  List<double>? scaling;

  ///      Sets an additional matrix that shifts (translates) the projection matrix.
  ///     The shift parameters are specified in NDC coordinates.
  /// Its sent as List of 2 double elements :
  ///      *  xshift    horizontal shift in NDC coordinates applied after the projection
  ///      *  yshift    vertical shift in NDC coordinates applied after the projection
  List<double>? shift;

  ///Mode of the camera that operates on.
  Mode? _mode;

  ///The world-space position of interest, which defaults to (x:0,y:0,z:-4).
  PlayxPosition? targetPosition;

  ///The orientation for the home position, which defaults to (x:0,y:1,z:0).
  PlayxPosition? upVector;

  ///The scroll delta multiplier, which defaults to 0.01.
  double? zoomSpeed;
  //orbit
  ///The initial eye position in world space for ORBIT mode.
  ///This defaults to (x:0,y:0,z:1).
  PlayxPosition? orbitHomePosition;

  ///Sets the multiplier with viewport delta for ORBIT mode.This defaults to 0.01
  ///List of 2 double :[x,y]
  List<double>? orbitSpeed;

  ///The FOV axis that's held constant when the viewport changes.
  ///This defaults to Vertical.
  Fov? fovDirection;

  ///The full FOV (not the half-angle) in the degrees.
  ///This defaults to 33.
  double? fovDegrees;

  ///The distance to the far plane, which defaults to 5000.
  double? farPlane;
  //map

  ///The ground plane size used to compute the home position for MAP mode.
  ///This defaults to 512 x 512
  List<double>? mapExtent;

  ///Constrains the zoom-in level. Defaults to 0.
  double? mapMinDistance;
  //freeflight
  ///The initial eye position in world space for FREE_FLIGHT mode.
  ///Defaults to (x:0,y:0,z:0).
  PlayxPosition? flightStartPosition;

  ///The initial orientation in pitch and yaw for FREE_FLIGHT mode.
  ///Defaults to [0,0].
  List<double>? flightStartOrientation;

  ///The maximum camera translation speed in world units per second for FREE_FLIGHT mode.
  ///Defaults to 10.
  double? flightMaxMoveSpeed;

  ///The number of speed steps adjustable with scroll wheel for FREE_FLIGHT mode.
  /// Defaults to 80.
  num? flightSpeedSteps;

  ///Applies a deceleration to camera movement in FREE_FLIGHT mode. Defaults to 0 (no damping).
  ///Lower values give slower damping times. A good default is 15.0. Too high a value may lead to instability.
  double? flightMoveDamping;

  ///The ground plane equation used for ray casts. This is a plane equation as in Ax + By + Cz + D = 0. Defaults to (0, 0, 1, 0).
  List<double>? groundPlane;

  ///Creates a camera on orbit mode.
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

  ///Creates a camera on map mode.
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

  ///Creates a camera on free flight mode.
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
