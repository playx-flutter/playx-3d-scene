import 'package:playx_3d_scene/models/model/animation.dart';
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
  GltfModel._(
      {super.assetPath,
      this.prefix,
      this.postfix,
      super.url,
      super.fallback,
      super.scale,
      super.animation});

  factory GltfModel.asset(
    String path, {
    String? prefix,
    String? postfix,
    Model? fallback,
    double? scale,
    PlayxAnimation? animation,
  }) {
    return GltfModel._(
        assetPath: path,
        prefix: prefix,
        postfix: postfix,
        fallback: fallback,
        scale: scale,
        animation: animation);
  }

  factory GltfModel.url(
    String url, {
    String? prefix,
    String? postfix,
    Model? fallback,
    double? scale,
    PlayxAnimation? animation,
  }) {
    return GltfModel._(
        url: url,
        prefix: prefix,
        postfix: postfix,
        fallback: fallback,
        scale: scale,
        animation: animation);
  }

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'prefix': prefix,
        'postfix': postfix,
        'fallback': fallback?.toJson(),
        'scale': scale,
        'animation': animation?.toJson(),
        'isGlb': false,
      };
}
