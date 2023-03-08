import 'package:playx_3d_scene/models/scene/light/light.dart';

class KtxLight extends Light {
  KtxLight._({super.assetPath, super.url, super.intensity});

  factory KtxLight.asset(String path, {double? intensity}) {
    return KtxLight._(assetPath: path, intensity: intensity);
  }

  factory KtxLight.url(String url, {double? intensity}) {
    return KtxLight._(url: url, intensity: intensity);
  }

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'intensity': intensity,
        'lightType': 1
      };
}
