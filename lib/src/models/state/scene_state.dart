/// Represents the state of the scene.
enum SceneState {
  /// represents idle state.
  none,

  /// represents loading state.
  loading,

  /// represents the scene has finished loading successfully.
  loaded,

  /// represents that some error happened while loading the scene.
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
