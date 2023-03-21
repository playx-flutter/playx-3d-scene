import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';
import 'package:playx_3d_scene/utils/utils.dart';

class HdrSkybox extends Skybox {
  ///Indicates whether the sun should be rendered. The sun can only be rendered
  ///if there is at least one light of type LightManager.Type.SUN in the Scene.
  ///The default value is false.
  bool showSun = false;

  HdrSkybox.asset(
    String path, {
    bool showSun = false,
  }) : super(assetPath: path);

  HdrSkybox.url(
    String url, {
    bool showSun = false,
  }) : super(url: url);

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'color': color?.toHex(),
        'showSun': showSun,
        'skyboxType': 2
      };
}
