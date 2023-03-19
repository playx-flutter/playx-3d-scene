import 'package:playx_3d_scene/models/scene/geometry/size.dart';
import 'package:playx_3d_scene/models/shapes/shape.dart';

class Cube extends Shape {
  double length;

  Cube(
      {required super.id,
      required this.length,
      required super.centerPosition,
      super.material})
      : super(size: PlayxSize.all(length));

  Map<String, dynamic> toJson() => {
        'id': id,
        'centerPosition': centerPosition?.toJson(),
        'size': size.toJson(),
        'material': material?.toJson(),
        'shapeType': 2
      };
}
