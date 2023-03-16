enum MagFilter {
  /// No filtering. Nearest neighbor is used.
  nearest,

  /// Box filtering. Weighted average of 4 neighbors is used.
  linear;

  String toName() {
    switch (this) {
      case MagFilter.nearest:
        return "NEAREST";
      case MagFilter.linear:
        return "LINEAR";
    }
  }
}
