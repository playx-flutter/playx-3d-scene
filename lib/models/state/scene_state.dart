enum SceneState {
  none,
  loading,
  loaded,
  error;

  static SceneState from(String? state) {
    switch (state) {
      case "NONE":
        return SceneState.none;

      case "LOADING":
        return SceneState.loading;

      case "LOADED":
        return SceneState.loaded;

      case "ERROR":
        return SceneState.error;

      default:
        return SceneState.none;
    }
  }
}
