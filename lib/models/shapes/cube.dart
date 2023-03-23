import 'package:playx_3d_scene/models/scene/geometry/size.dart';
import 'package:playx_3d_scene/models/shapes/shape.dart';

/// An object that represents a cube shape to be rendered.
class Cube extends Shape {
  /// Length of the cube.
  double length;
  late PlayxSize _size;

  Cube(
      {required super.id,
      required this.length,
      required super.centerPosition,
      super.material})
      : super() {
    _size = PlayxSize.all(length);
  }

  @override
  Map<String, dynamic> toJson() => {
        'id': id,
        'centerPosition': centerPosition?.toJson(),
        'size': _size.toJson(),
        'material': material?.toJson(),
        'shapeType': 2
      };
}
