class LensProjection {
  double focalLength;
  double? aspect;
  double? near;
  double? far;

  LensProjection({required this.focalLength, this.aspect, this.near, this.far});

  Map<String, dynamic> toJson() {
    return {
      "focalLength": focalLength,
      "aspect": aspect,
      "near": near,
      "far": far,
    };
  }
}
