import 'package:playx_3d_scene/models/scene/geometry/direction.dart';
import 'package:playx_3d_scene/models/scene/geometry/position.dart';
import 'package:playx_3d_scene/models/scene/material/material.dart';

/// An object that represents shapes to be rendered on the scene.
///
/// See also:
/// [Cube]
/// [Plane]
/// [Sphere]
class Shape {
  /// id of the shape to be used to update shapes.
  int id;

  /// center position of the shape in the world space.
  PlayxPosition? centerPosition;

  /// direction of the shape rotation in the world space
  PlayxDirection? normal;

  /// material to be used for the shape.
  PlayxMaterial? material;

  Shape({required this.id, this.centerPosition, this.normal, this.material});

  Map<String, dynamic> toJson() => {
        'id': id,
        'centerPosition': centerPosition?.toJson(),
        'normal': normal?.toJson(),
        'material': material?.toJson(),
        'type': 0
      };
}
