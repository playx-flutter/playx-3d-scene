class PlayxSize {
  double width = 0.0;
  double height = 0.0;

  PlayxSize({this.width = 0.0, this.height = 0.0});

  PlayxSize.all(double v) {
    width = v;
    height = v;
  }

  Map<String, dynamic> toJson() => {
        'x': width,
        'y': 0,
        'z': height,
      };
}
