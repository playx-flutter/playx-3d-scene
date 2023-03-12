///Denotes the projection type used by this camera.
enum ProjectionType {
  /// Perspective projection, objects get smaller as they are farther.
  perspective,

  /// Orthonormal projection, preserves distances.
  ortho;

  String toName() {
    switch (this) {
      case ProjectionType.perspective:
        return "PERSPECTIVE";
      case ProjectionType.ortho:
        return "ORTHO";
      default:
        return "PERSPECTIVE";
    }
  }
}
