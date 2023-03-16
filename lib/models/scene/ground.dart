import 'package:playx_3d_scene/models/scene/geometry/direction.dart';
import 'package:playx_3d_scene/models/scene/geometry/size.dart';

import 'geometry/position.dart';
import 'material/material.dart';

class Ground {
  PlayxPosition? centerPosition;
  PlayxSize size;
  PlayxDirection? normal;
  bool isBelowModel = true;
  PlayxMaterial? material;

  Ground(
      {required this.size,
      this.centerPosition,
      this.normal,
      this.isBelowModel = false,
      this.material});

  Map<String, dynamic> toJson() => {
        'centerPosition': centerPosition?.toJson(),
        'normal': normal?.toJson(),
        'isBelowModel': isBelowModel,
        'size': size.toJson(),
        'material': material?.toJson(),
      };
}
