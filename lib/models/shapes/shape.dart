import 'package:playx_3d_scene/models/scene/geometry/direction.dart';
import 'package:playx_3d_scene/models/scene/geometry/position.dart';
import 'package:playx_3d_scene/models/scene/geometry/size.dart';
import 'package:playx_3d_scene/models/scene/material/material.dart';

class Shape {
  int id;
  PlayxPosition? centerPosition;
  PlayxSize size;
  PlayxDirection? normal;
  PlayxMaterial? material;

  Shape(
      {required this.id,
      required this.size,
      this.centerPosition,
      this.normal,
      this.material});

  Map<String, dynamic> toJson() => {
        'id': id,
        'centerPosition': centerPosition?.toJson(),
        'normal': normal?.toJson(),
        'size': size.toJson(),
        'material': material?.toJson(),
        'type': 0
      };
}
