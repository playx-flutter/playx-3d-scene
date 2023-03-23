/// An object that represents position information as coordinates in the 3d world space.
/// By providing x, y and z coordinates.
///
/// Defaults to (x:0, y:0, z:0).
class PlayxPosition {
  double x = 0.0;
  double y = 0.0;
  double z = 0.0;

  PlayxPosition({this.x = 0.0, this.y = 0.0, this.z = 0.0});

  PlayxPosition.all(double v) {
    x = v;
    y = v;
    z = v;
  }

  Map<String, dynamic> toJson() => {
        'x': x,
        'y': y,
        'z': z,
      };
}
