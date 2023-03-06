import 'package:playx_3d_scene/models/model/animation.dart';

import 'model.dart';

class GlbModel extends Model {
  GlbModel._(
      {super.assetPath,
      super.url,
      super.fallback,
      super.scale,
      super.animation});

  factory GlbModel.asset(
    String? path, {
    Model? fallback,
    double? scale,
    PlayxAnimation? animation,
  }) {
    return GlbModel._(
        assetPath: path,
        fallback: fallback,
        scale: scale,
        animation: animation);
  }

  factory GlbModel.url(
    String url, {
    Model? fallback,
    double? scale,
    PlayxAnimation? animation,
  }) {
    return GlbModel._(
        url: url, fallback: fallback, scale: scale, animation: animation);
  }

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'fallback': fallback?.toJson(),
        'scale': scale,
        'animation': animation?.toJson(),
        'isGlb': true,
      };
}