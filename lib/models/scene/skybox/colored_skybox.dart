import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';
import 'package:playx_3d_scene/utils/utils.dart';

/// An object that represents skybox based that shows a color only.
class ColoredSkybox extends Skybox {
  ColoredSkybox({required super.color});

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'color': color?.toHex(),
        'skyboxType': 3
      };
}
