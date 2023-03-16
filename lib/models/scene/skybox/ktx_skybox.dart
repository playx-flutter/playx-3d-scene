import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';

class KtxSkybox extends Skybox {
  KtxSkybox._({super.assetPath, super.url});

  factory KtxSkybox.asset(String path) {
    return KtxSkybox._(assetPath: path);
  }

  factory KtxSkybox.url(String url) {
    return KtxSkybox._(url: url);
  }

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'color': color?.toHex(),
        'skyboxType': 1
      };
}
