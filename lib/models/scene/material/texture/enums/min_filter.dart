enum MinFilter {
  /// No filtering. Nearest neighbor is used.
  nearest,

  /// Box filtering. Weighted average of 4 neighbors is used.
  linear,

  /// Mip-mapping is activated. But no filtering occurs.
  nearestMipmapNearest,

  /// Box filtering within a mip-map level.
  linearMipmapNearest,

  /// Mip-map levels are interpolated, but no other filtering occurs.
  nearestMipmapLinear,

  /// Both interpolated Mip-mapping and linear filtering are used.
  linearMipmapLinear;

  String toName() {
    switch (this) {
      case MinFilter.nearest:
        return "NEAREST";
      case MinFilter.linear:
        return "LINEAR";
      case MinFilter.nearestMipmapNearest:
        return "NEAREST_MIPMAP_NEAREST";
      case MinFilter.linearMipmapNearest:
        return "LINEAR_MIPMAP_NEAREST";
      case MinFilter.nearestMipmapLinear:
        return "NEAREST_MIPMAP_LINEAR";
      case MinFilter.linearMipmapLinear:
        return "LINEAR_MIPMAP_LINEAR";
    }
  }
}
