import 'package:playx_3d_scene/models/scene/material/material_paramater.dart';

class PlayxMaterial {
  //for material
  String? assetPath;
  String? url;
  List<MaterialParameter>? parameters;

  PlayxMaterial.asset(this.assetPath, {this.parameters});

  PlayxMaterial.url(this.url, {this.parameters});

  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'parameters': parameters?.map((param) => param.toJson()).toList()
      };
}
