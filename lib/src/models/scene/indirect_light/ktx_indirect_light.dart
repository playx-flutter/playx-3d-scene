import 'package:playx_3d_scene/src/models/scene/indirect_light/indirect_light.dart';

/// An object that represents indirect light that is created from ktx file format.
///
/// To extract indirect light from images, Use the cmgen tool to generate the indirect light data as ktx file format.
class KtxIndirectLight extends IndirectLight {
  /// creates a new indirect light from ktx file format from assets.
  KtxIndirectLight.asset(String path, {super.intensity})
      : super(assetPath: path);

  /// creates a new indirect light from ktx file format from url.
  KtxIndirectLight.url(String url, {super.intensity}) : super(url: url);

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'intensity': intensity,
        'lightType': 1
      };

  @override
  String toString() {
    return 'KtxIndirectLight(assetPath: $assetPath, url: $url, intensity: $intensity)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is KtxIndirectLight && super == other;
  }

  @override
  int get hashCode {
    final hash = super.hashCode;
    return hash;
  }
}
