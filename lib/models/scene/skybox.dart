import 'dart:math';
import 'dart:ui';

class Skybox {
  /// Environment Color.
  /// Changes the background color for the scene.
  /// if not provided and environment asset path is not provided,
  /// A Transparent color will be used.
  Color? color;

  /// environment asset path used to load KTX FILE from assets.
  /// changes scene skybox from images converted to KTX FILE.
  /// Filament provides an offline tool called cmgen
  /// that can consume an image
  /// and produce Light and skybox ktx files in one fell swoop.
  String? assetPath;

  /// environment url used to load KTX FILE from web.
  String? url;

  Skybox._({this.color, this.assetPath, this.url});

  factory Skybox.asset(String? path, {Color? color}) {
    return Skybox._(assetPath: path, color: color);
  }

  factory Skybox.url(String? url, {Color? color}) {
    return Skybox._(url: url, color: color);
  }

  factory Skybox.color(Color? color) {
    return Skybox._(color: color);
  }

  Map<String, dynamic> toJson() => {
        'color': toInt(color),
        'assetPath': assetPath,
        'url': url,
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
