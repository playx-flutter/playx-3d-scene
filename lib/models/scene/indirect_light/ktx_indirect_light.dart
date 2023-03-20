import 'package:playx_3d_scene/models/scene/indirect_light/indirect_light.dart';

class KtxIndirectLight extends IndirectLight {
  KtxIndirectLight.asset(String path, {double? intensity})
      : super(assetPath: path);

  KtxIndirectLight.url(String url, {double? intensity}) : super(url: url);

  @override
  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'intensity': intensity,
        'lightType': 1
      };
}
