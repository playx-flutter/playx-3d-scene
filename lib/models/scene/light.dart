class Light {
  /// light asset path used to load KTX FILE from assets.
  /// used to change indirect lighting from Image-Based Light.
  String? assetPath;

  /// light url used to load KTX FILE from assets.
  /// used to change indirect lighting from Image-Based Light.
  String? url;

  /// indirect light intensity.
  /// can be used with light asset path.
  /// or create default light with certain intensity.

  double? intensity;

  Light._({this.assetPath, this.url, this.intensity});

// Float3 target;
  // Float3 position;

  factory Light.asset(String? path, {double? intensity}) {
    return Light._(assetPath: path, intensity: intensity);
  }

  factory Light.url(String? url, {double? intensity}) {
    return Light._(url: url, intensity: intensity);
  }

  factory Light.indirectLight(double? intensity) {
    return Light._(intensity: intensity);
  }

  Map<String, dynamic> toJson() => {
        'intensity': intensity,
        'assetPath': assetPath,
        'url': url,
      };
}
