import 'package:playx_3d_scene/src/models/model/model.dart';

/// represents object of model that will be loaded from gltf file.
///
///glTF is a 3D file format maintained by the Khronos Group.
class GltfModel extends Model {
  /// Prefix path for gltf image assets to be added before image path.
  ///
  /// if the images path that in the gltf file different from the flutter asset path,
  /// consider adding prefix to the images path to be before the image.
  ///
  /// For example, if the image path in the gltf file is textures/texture.png
  /// and in assets the image path is assets/models/textures/texture.png
  /// you will need to add prefix to be 'assets/models/'.
  String prefix = "";

  /// postfix path for gltf image assets to be added after image path.
  ///
  /// if the images path that in the gltf file different from the flutter asset path,
  /// consider adding to the images path to be after the image.
  ///
  /// For example, if the image path in the gltf file is assets/textures/texture
  /// and in assets the image path is assets/textures/texture.png
  /// you will need to add prefix to be '.png'.
  String postfix = "";

  /// creates gltf model based on the  file asset path.
  GltfModel.asset(
    String path, {
    this.prefix = "",
    this.postfix = "",
    super.fallback,
    super.scale,
    super.centerPosition,
    super.animation,
  }) : super(assetPath: path);

  /// creates gltf model based on glb file url .
  /// currently supporting only .zip file format.
  GltfModel.url(
    String url, {
    this.prefix = "",
    this.postfix = "",
    super.fallback,
    super.scale,
    super.centerPosition,
    super.animation,
  }) : super(url: url);

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'pathPrefix': prefix,
        'pathPostfix': postfix,
        'fallback': fallback?.toJson(),
        'scale': scale,
        'centerPosition': centerPosition?.toJson(),
        'animation': animation?.toJson(),
        'isGlb': false,
      };

  @override
  String toString() {
    return 'GltfModel(assetPath: $assetPath, url: $url, prefix: $prefix, postfix: $postfix, fallback: $fallback, scale: $scale, centerPosition: $centerPosition, animation: $animation)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is GltfModel &&
        other.assetPath == assetPath &&
        other.url == url &&
        other.prefix == prefix &&
        other.postfix == postfix &&
        other.fallback == fallback &&
        other.scale == scale &&
        other.centerPosition == centerPosition &&
        other.animation == animation;
  }

  @override
  int get hashCode {
    return assetPath.hashCode ^
        url.hashCode ^
        prefix.hashCode ^
        postfix.hashCode ^
        fallback.hashCode ^
        scale.hashCode ^
        centerPosition.hashCode ^
        animation.hashCode;
  }
}
