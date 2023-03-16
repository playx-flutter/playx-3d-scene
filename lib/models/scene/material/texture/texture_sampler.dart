import 'package:playx_3d_scene/models/scene/material/texture/enums/mag_filter.dart';
import 'package:playx_3d_scene/models/scene/material/texture/enums/min_filter.dart';
import 'package:playx_3d_scene/models/scene/material/texture/enums/wrap_mode.dart';

class PlayxTextureSampler {
  MinFilter min;

  MagFilter mag;
  WrapMode wrap;
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
