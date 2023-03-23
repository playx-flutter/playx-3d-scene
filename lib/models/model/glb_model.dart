import 'model.dart';

/// represents object of model that will be loaded from glb file.
///
/// GLB is a binary container format of glTF. It bundles all the textures and mesh data into a single file.
class GlbModel extends Model {
  /// creates glb model based on glb file asset path.
  GlbModel.asset(String path,
      {super.fallback, super.scale, super.centerPosition, super.animation})
      : super(assetPath: path);

  /// creates glb model based on glb file url.
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
