import 'package:flutter/foundation.dart';
import 'package:playx_3d_scene/src/models/scene/material/material_parameter.dart';

/// An object that defines the visual appearance of a surface.
/// Filament offers a customizable material system
/// that you can use to create both simple and complex materials.
/// Materials are defined in a .mat file that describes all the information required by a material.
/// To use the .mat file in the app, Use matc tool in filament to convert .mat files to .filmat files.
/// For more information about materials, see the filament material documentation
/// * https://google.github.io/filament/Materials.html
/// * https://google.github.io/filament/Material%20Properties.pdf
class PlayxMaterial {
  /// Asset path of the .filmat material file
  String? assetPath;

  /// url of the .filmat material file
  String? url;

  ///Material parameters that can be used.
  List<MaterialParameter>? parameters;

  /// Creates material object from the .filmat material file from assets
  PlayxMaterial.asset(this.assetPath, {this.parameters});

  /// Creates material object from the .filmat material file from url.
  PlayxMaterial.url(this.url, {this.parameters});

  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'parameters': parameters?.map((param) => param.toJson()).toList()
      };

  @override
  String toString() {
    return 'PlayxMaterial(assetPath: $assetPath, url: $url, parameters: $parameters)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is PlayxMaterial &&
        other.assetPath == assetPath &&
        other.url == url &&
        listEquals(other.parameters, parameters);
  }

  @override
  int get hashCode => assetPath.hashCode ^ url.hashCode ^ parameters.hashCode;
}
