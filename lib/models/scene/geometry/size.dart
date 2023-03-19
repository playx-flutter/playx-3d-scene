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
}
