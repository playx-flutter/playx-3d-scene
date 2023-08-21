///Denotes a field-of-view direction.
enum Fov {
  /// The field-of-view angle is defined on the vertical axis.
  vertical,

  /// The field-of-view angle is defined on the horizontal axis.
  horizontal;

  String toName() {
    switch (this) {
      case Fov.vertical:
        return "VERTICAL";
      case Fov.horizontal:
        return "HORIZONTAL";
      default:
        return "VERTICAL";
    }
  }
}
