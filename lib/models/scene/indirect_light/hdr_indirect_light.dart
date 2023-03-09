import 'package:playx_3d_scene/models/scene/indirect_light/indirect_light.dart';

class HdrIndirectLight extends IndirectLight {
  HdrIndirectLight._({super.assetPath, super.url, super.intensity});

  factory HdrIndirectLight.asset(String path, {double? intensity}) {
    return HdrIndirectLight._(assetPath: path, intensity: intensity);
  }

  factory HdrIndirectLight.url(String url, {double? intensity}) {
    return HdrIndirectLight._(url: url, intensity: intensity);
  }

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'intensity': intensity,
        'lightType': 2
      };
}
