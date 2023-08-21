import 'package:playx_3d_scene/src/models/model/animation.dart';
import 'package:playx_3d_scene/src/models/model/glb_model.dart';
import 'package:playx_3d_scene/src/models/model/gltf_model.dart';
import 'package:playx_3d_scene/src/models/scene/geometry/position.dart';

/// represents base object of the 3d model to be rendered.
///
/// see also :
///
/// [GlbModel] :
/// [GltfModel] :
abstract class Model {
  /// Model asset path to load the model from assets.
  String? assetPath;

  /// Model url to load the model from url.
  String? url;

  /// Model to be shown when error happened.
  ///
  /// can be whether [GlbModel] or [GltfModel]
  Model? fallback;

  /// Scale Factor of the model.
  /// Should be greater than 0.
  /// Defaults to 1.
  double? scale;

  ///Coordinate of center point position of the rendered model.
  ///
  /// Defaults to ( x:0,y: 0,z: -4)
  PlayxPosition? centerPosition;

  ///Controls what animation should be played by the rendered model.
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
