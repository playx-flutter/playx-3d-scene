///Camera Modes that operates on.
///Three modes are supported: ORBIT, MAP, and FREE_FLIGHT.
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
