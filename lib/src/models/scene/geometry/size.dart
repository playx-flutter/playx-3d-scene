/// An object that represents shape size in the world space.
class PlayxSize {
  double x = 0.0;
  double y = 0.0;
  double z = 0.0;

  PlayxSize({this.x = 0.0, this.y = 0.0, this.z = 0.0});

  PlayxSize.all(double v) {
    x = v;
    y = v;
    z = v;
  }

  Map<String, dynamic> toJson() => {
        'x': x,
        'y': y,
        'z': z,
      };

  @override
  String toString() => 'PlayxSize(x: $x, y: $y, z: $z)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is PlayxSize && other.x == x && other.y == y && other.z == z;
  }

  @override
  int get hashCode => x.hashCode ^ y.hashCode ^ z.hashCode;
}
