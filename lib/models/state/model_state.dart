/// Represents the state of the model.
enum ModelState {
  /// represents idle state.
  none,

  /// represents loading state.
  loading,

  /// represents the model has loaded successfully.
  loaded,

  /// represents the model has failed loading and it loaded the fallback model successfully.
  fallbackLoaded,

  /// represents the model and fallback model have failed loading.
  error;

  static ModelState from(String? state) {
    switch (state) {
      case "NONE":
        return ModelState.none;

      case "LOADING":
        return ModelState.loading;

      case "LOADED":
        return ModelState.loaded;

      case "FALLBACK_LOADED":
        return ModelState.fallbackLoaded;

      case "ERROR":
        return ModelState.error;

      default:
        return ModelState.none;
    }
  }
}
