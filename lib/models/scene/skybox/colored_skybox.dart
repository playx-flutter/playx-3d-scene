import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';

class ColoredSkybox extends Skybox {
  ColoredSkybox({required super.color});

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'color': toInt(color),
        'skyboxType': 3
      };
}
