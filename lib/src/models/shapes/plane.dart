import 'package:playx_3d_scene/src/models/scene/geometry/size.dart';
import 'package:playx_3d_scene/src/models/shapes/shape.dart';

/// An object that represents a plane shape to be rendered.
class Plane extends Shape {
  /// size of the plane in the world space.
  /// provides the width and height of the plane in the world space.
  /// should provide only 2 coordinates of the plane.
  /// To draw horizontally y must be 0.
  /// To draw vertically z must be 0.
  PlayxSize size;

  Plane(
      {required super.id,
      required this.size,
      required super.centerPosition,
      super.normal,
      super.material});

  @override
  Map<String, dynamic> toJson() => {
        'id': id,
        'centerPosition': centerPosition?.toJson(),
        'normal': normal?.toJson(),
        'size': size.toJson(),
        'material': material?.toJson(),
        'shapeType': 1
      };

  @override
  String toString() {
    return 'Plane(id: $id, size: $size, centerPosition: $centerPosition)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is Plane &&
        other.id == id &&
        other.size == size &&
        other.centerPosition == centerPosition;
  }

  @override
  int get hashCode => id.hashCode ^ size.hashCode ^ centerPosition.hashCode;
}
