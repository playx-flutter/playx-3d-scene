/// An object that represents the direction of the shape in the world space.
///
/// value of each coordinate must be 0 or 1 or -1
class PlayxDirection {
  double x = 0.0;
  double y = 0.0;
  double z = 0.0;

  PlayxDirection({this.x = 0, this.y = 0, this.z = 0});

  PlayxDirection.x(this.x);

  PlayxDirection.y(this.y);

  PlayxDirection.z(this.z);

  Map<String, dynamic> toJson() => {
        'x': x,
        'y': y,
        'z': z,
      };

  @override
  String toString() => 'PlayxDirection(x: $x, y: $y, z: $z)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is PlayxDirection &&
        other.x == x &&
        other.y == y &&
        other.z == z;
  }

  @override
  int get hashCode => x.hashCode ^ y.hashCode ^ z.hashCode;
}
