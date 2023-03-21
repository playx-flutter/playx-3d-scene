import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';
import 'package:playx_3d_scene/utils/utils.dart';

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
