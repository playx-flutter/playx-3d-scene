import 'model.dart';

class GlbModel extends Model {
  GlbModel.asset(String path,
      {super.fallback, super.scale, super.centerPosition, super.animation})
      : super(assetPath: path);

  GlbModel.url(String url,
      {super.fallback, super.scale, super.centerPosition, super.animation})
      : super(url: url);

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'fallback': fallback?.toJson(),
        'scale': scale,
        'centerPosition': centerPosition?.toJson(),
        'animation': animation?.toJson(),
        'isGlb': true,
      };
}
