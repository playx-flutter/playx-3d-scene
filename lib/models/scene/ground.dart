import 'package:playx_3d_scene/models/scene/geometry/size.dart';
import 'package:playx_3d_scene/models/shapes/plane.dart';

class Ground extends Plane {
  double width;
  double height;
  bool isBelowModel = true;

  Ground(
      {required this.width,
      required this.height,
      super.centerPosition,
      super.normal,
      this.isBelowModel = false,
      super.material})
      : super(size: PlayxSize(x: width, z: height), id: 0);

  Map<String, dynamic> toJson() => {
        'centerPosition': centerPosition?.toJson(),
        'normal': normal?.toJson(),
        'isBelowModel': isBelowModel,
        'size': size.toJson(),
        'material': material?.toJson(),
      };
}
