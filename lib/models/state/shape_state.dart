/// Represents the state of loading shapes.
enum ShapeState {
  /// represents idle state.
  none,

  /// represents loading state.
  loading,

  /// represents the shaped has finished loading successfully.
  loaded,

  /// represents that some error happened while loading the shapes.
  error;

  static ShapeState from(String? state) {
    switch (state) {
      case "NONE":
        return ShapeState.none;
      case "LOADING":
        return ShapeState.loading;
      case "LOADED":
        return ShapeState.loaded;
      case "ERROR":
        return ShapeState.error;

      default:
        return ShapeState.none;
    }
  }
}
