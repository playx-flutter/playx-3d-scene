import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';

class HdrSkybox extends Skybox {
  ///Indicates whether the sun should be rendered. The sun can only be rendered
  ///if there is at least one light of type LightManager.Type.SUN in the Scene.
  ///The default value is false.
  bool showSun;

  HdrSkybox._({super.assetPath, super.url, this.showSun = false});

  factory HdrSkybox.asset(
    String path, {
    bool showSun = false,
  }) {
    return HdrSkybox._(assetPath: path, showSun: showSun);
  }

  factory HdrSkybox.url(
    String url, {
    bool showSun = false,
  }) {
    return HdrSkybox._(url: url, showSun: showSun);
  }

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'color': color?.toHex(),
        'showSun': showSun,
        'skyboxType': 2
      };
}
