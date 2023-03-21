import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';
import 'package:playx_3d_scene/utils/utils.dart';

class KtxSkybox extends Skybox {
  KtxSkybox.asset(String path) : super(assetPath: path);

  KtxSkybox.url(String url) : super(url: url);

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'color': color?.toHex(),
        'skyboxType': 1
      };
}
