enum Mode {
  orbit,
  map,
  freeFlight;

  String toName() {
    switch (this) {
      case Mode.orbit:
        return "ORBIT";
      case Mode.map:
        return "MAP";
      case Mode.freeFlight:
        return "FREE_FLIGHT";
      default:
        return "ORBIT";
    }
  }
}
