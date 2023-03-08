import 'package:playx_3d_scene/models/scene/light/light.dart';

class HdrLight extends Light {
  HdrLight._({super.assetPath, super.url, super.intensity});

  factory HdrLight.asset(String path, {double? intensity}) {
    return HdrLight._(assetPath: path, intensity: intensity);
  }

  factory HdrLight.url(String url, {double? intensity}) {
    return HdrLight._(url: url, intensity: intensity);
  }

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'intensity': intensity,
        'lightType': 2
      };
}
