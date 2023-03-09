import 'package:playx_3d_scene/models/scene/light/light.dart';

class IndirectLight extends Light {
  IndirectLight(
      {super.intensity,
      super.radianceBands,
      super.radianceSh,
      super.irradianceBands,
      super.irradianceSh,
      super.rotation});

  @override
  Map<String, dynamic> toJson() => {
        'intensity': intensity,
        'radianceBands': radianceBands,
        'radianceSh': radianceSh,
        'irradianceBands': irradianceBands,
        'irradianceSh': irradianceSh,
        'rotation': rotation,
        'lightType': 3
      };
}
