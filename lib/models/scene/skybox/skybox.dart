import 'dart:ui';

import 'package:playx_3d_scene/utils/utils.dart';

abstract class Skybox {
  /// environment asset path used to load KTX FILE from assets.
  /// changes scene skybox from images converted to KTX FILE.
  /// Filament provides an offline tool called cmgen
  /// that can consume an image
  /// and produce Light and skybox ktx files in one fell swoop.
  String? assetPath;

  /// environment url used to load KTX FILE from web.
  String? url;

  /// Environment Color.
  /// Changes the background color for the scene.
  /// if not provided and environment asset path is not provided,
  /// A Transparent color will be used.
  Color? color;

  Skybox({
    this.color,
    this.assetPath,
    this.url,
  });

  Map<String, dynamic> toJson() => {
        'assetPath': assetPath,
        'url': url,
        'color': color?.toHex,
      };
}
