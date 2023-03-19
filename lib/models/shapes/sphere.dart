import 'package:playx_3d_scene/models/shapes/shape.dart';

class Sphere extends Shape {
  double radius;
  int? stacks;
  int? slices;

  Sphere(
      {required super.id,
      required super.centerPosition,
      required this.radius,
      this.stacks,
      this.slices,
      super.normal,
      super.material});

  Map<String, dynamic> toJson() => {
        'id': id,
        'centerPosition': centerPosition?.toJson(),
        'normal': normal?.toJson(),
        'radius': radius,
        'stacks': stacks,
        'slices': slices,
        'material': material?.toJson(),
        'shapeType': 3
      };
}
