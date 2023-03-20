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
}
