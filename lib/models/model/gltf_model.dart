import 'package:playx_3d_scene/models/model/model.dart';

class GltfModel extends Model {
  /// prefix for gltf image assets.
  /// if the images path that in the gltf file different from the flutter asset path,
  /// you can add prefix to the images path to be before the image.
  /// LIKE if in the gltf file, the image path is textures/texture.png
  /// and in assets the image path is assets/models/textures/texture.png
  /// you will need to add prefix to be 'assets/models/'.
  String? prefix;

  /// postfix path for gltf image assets.
  /// if the images path that in the gltf file different from the flutter asset path,
  /// you can add postfix to the images path to be after the image.
  /// LIKE if in the gltf file, the image path is assets/textures/texture
  /// and in assets the image path is assets/textures/texture.png
  /// you will need to add prefix to be '.png'.

  String? postfix;

  GltfModel.asset(
    String path, {
    this.prefix,
    this.postfix,
    super.fallback,
    super.scale,
    super.centerPosition,
    super.animation,
  }) : super(assetPath: path);

  GltfModel.url(
    String url, {
    this.prefix,
    this.postfix,
    super.fallback,
    super.scale,
    super.centerPosition,
    super.animation,
  }) : super(url: url);

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'prefix': prefix,
        'postfix': postfix,
        'fallback': fallback?.toJson(),
        'scale': scale,
        'centerPosition': centerPosition?.toJson(),
        'animation': animation?.toJson(),
        'isGlb': false,
      };
}
