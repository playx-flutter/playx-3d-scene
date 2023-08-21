///Wrap Mode to be used.
///
///See Also:
///[PlayxTextureSampler]
enum WrapMode {
  /// The edge of the texture extends to infinity.
  clampToEdge,

  /// The texture infinitely repeats in the wrap direction.
  repeat,

  /// The texture infinitely repeats and mirrors in the wrap direction.
  mirroredRepeat;

  String toName() {
    switch (this) {
      case WrapMode.clampToEdge:
        return "CLAMP_TO_EDGE";

      case WrapMode.repeat:
        return "REPEAT";

      case WrapMode.mirroredRepeat:
        return "MIRRORED_REPEAT";
    }
  }
}
