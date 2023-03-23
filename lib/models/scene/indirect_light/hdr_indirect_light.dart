import 'package:playx_3d_scene/models/scene/indirect_light/indirect_light.dart';

/// An object that represents indirect light that is created from hdr file format.
class HdrIndirectLight extends IndirectLight {
  /// creates a new indirect light from HDR file format from assets.
  HdrIndirectLight.asset(String path, {double? intensity})
      : super(assetPath: path);

  /// creates a new indirect light from HDR file format from url.
  HdrIndirectLight.url(String url, {double? intensity}) : super(url: url);

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'intensity': intensity,
        'lightType': 2
      };
}
