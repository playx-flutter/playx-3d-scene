enum ShapeState {
  none,
  loading,
  loaded,
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
