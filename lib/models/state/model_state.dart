enum ModelState {
  none,
  loading,
  loaded,
  fallbackLoaded,
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
