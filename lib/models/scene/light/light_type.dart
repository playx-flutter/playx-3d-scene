enum LightType {
  /// Directional light that also draws a sun's disk in the sky.
  sun,

  /// Directional light, emits light in a given direction.
  directional,

  /// Point light, emits light from a position, in all directions.
  point,

  /// Physically correct spot light.
  focusedSpot,

  /// Spot light with coupling of outer cone and illumination disabled.
  spot;

  static String toName(LightType? type) {
    switch (type) {
      case LightType.sun:
        return "SUN";
      case LightType.directional:
        return "DIRECTIONAL";
      case LightType.point:
        return "POINT";
      case LightType.focusedSpot:
        return "FOCUSED_SPOT";
      case LightType.spot:
        return "SPOT";
      default:
        return "DIRECTIONAL";
    }
  }
}
