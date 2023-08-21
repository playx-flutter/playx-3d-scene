import 'package:playx_3d_scene/src/models/shapes/shape.dart';

/// An object that represents a cube shape to be rendered.
class Sphere extends Shape {
  /// The radius of the constructed sphere.
  double radius;

  ///The number of stacks for the sphere.
  int? stacks;

  ///The number of slices for the sphere.
  int? slices;

  Sphere(
      {required super.id,
      required super.centerPosition,
      required this.radius,
      this.stacks,
      this.slices,
      super.normal,
      super.material});

  @override
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
