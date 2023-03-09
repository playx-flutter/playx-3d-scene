import 'package:playx_3d_scene/models/scene/indirect_light/indirect_light.dart';

class KtxIndirectLight extends IndirectLight {
  KtxIndirectLight._({super.assetPath, super.url, super.intensity});

  factory KtxIndirectLight.asset(String path, {double? intensity}) {
    return KtxIndirectLight._(assetPath: path, intensity: intensity);
  }

  factory KtxIndirectLight.url(String url, {double? intensity}) {
    return KtxIndirectLight._(url: url, intensity: intensity);
  }

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'intensity': intensity,
        'lightType': 1
      };
}
