import 'package:playx_3d_scene/models/model/animation.dart';
import 'package:playx_3d_scene/models/model/glb_model.dart';
import 'package:playx_3d_scene/models/model/gltf_model.dart';

abstract class Model {
  /// glb or gltf model asset path to be loaded from assets.
  String? assetPath;

  /// glb or gltf model url to be loaded from url.
  String? url;

  /// glb or gltf model to be shown when error happened.
  Model? fallback;

  /// scale of the model
  /// should be greater than 0
  // default is 1f;
  double? scale;

  ///centerPoint Coordinate of center point of unit cube,
  /// must provide x, y, and z coordinates.
  /// as an array of 3 elements.
  /// default is [ 0, 0, -4 ]
  List<double>? centerPosition;

  ///control what animation should be played by glb or gltf model animation.
  PlayxAnimation? animation;

  Model(
      {this.assetPath,
      this.url,
      this.fallback,
      this.scale = 1.0,
      this.centerPosition,
      this.animation});

  Map<String, dynamic> toJson() {
    if (this is GlbModel) {
      return (this as GlbModel).toJson();
    } else if (this is GltfModel) {
      return (this as GltfModel).toJson();
    } else {
      return {};
    }
  }
}
