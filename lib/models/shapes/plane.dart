import 'package:playx_3d_scene/models/scene/geometry/size.dart';
import 'package:playx_3d_scene/models/shapes/shape.dart';

class Plane extends Shape {
  PlayxSize size;

  Plane(
      {required super.id,
      required this.size,
      required super.centerPosition,
      super.normal,
      super.material});

  Map<String, dynamic> toJson() => {
        'id': id,
        'centerPosition': centerPosition?.toJson(),
        'normal': normal?.toJson(),
        'size': size.toJson(),
        'material': material?.toJson(),
        'shapeType': 1
      };
}
