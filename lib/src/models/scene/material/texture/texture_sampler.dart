import 'package:playx_3d_scene/src/models/scene/material/texture/enums/mag_filter.dart';
import 'package:playx_3d_scene/src/models/scene/material/texture/enums/min_filter.dart';
import 'package:playx_3d_scene/src/models/scene/material/texture/enums/wrap_mode.dart';

/// An object that defines how a texture is accessed.
class PlayxTextureSampler {
  ///Minification filter to be used.
  /// Defaults to LINEAR_MIPMAP_LINEAR
  MinFilter min;

  ///Magnification filter to be used.
  /// Defaults to LINEAR
  MagFilter mag;

  /// Wrap mode to be used
  /// Defaults to REPEAT
  WrapMode wrap;

  ///Amount of anisotropy, controls anisotropic filtering, should be a power-of-two. The default is 0. The maximum permissible value is 7.
  double? anisotropy;

  PlayxTextureSampler(
      {this.min = MinFilter.linearMipmapLinear,
      this.mag = MagFilter.linear,
      this.wrap = WrapMode.clampToEdge,
      this.anisotropy});

  Map<String, dynamic> toJson() => {
        'min': min.toName(),
        'mag': mag.toName(),
        'wrap': wrap.toName(),
        'anisotropy': anisotropy,
      };
}
