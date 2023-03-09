import 'dart:math';
import 'dart:ui';

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
        'color': toInt(color),
      };
}

num? toInt(Color? color) {
  if (color == null) return null;

  num value = color.value;
  while (value > pow(2, 31)) {
    value = value - pow(2, 32);
  }
  return value;
}
