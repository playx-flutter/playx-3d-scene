enum MaterialType {
  //color can be presented by int or Color like Colors.white
  color,

  /// Single boolean or Vector of 2 to 4 booleans
  ///used for material bool type, bool2,bool3,bool4 types
  bool,
  boolVector,
  float,
  floatVector,
  int,
  intVector,
  mat3,
  mat4,
  texture;

  String toName() {
    switch (this) {
      case MaterialType.color:
        return "COLOR";
      case MaterialType.bool:
        return "BOOL";
      case MaterialType.boolVector:
        return "BOOL_VECTOR";
      case MaterialType.float:
        return "FLOAT";
      case MaterialType.floatVector:
        return "FLOAT_VECTOR";
      case MaterialType.int:
        return "INT";
      case MaterialType.intVector:
        return "INT_VECTOR";
      case MaterialType.mat3:
        return "MAT3";
      case MaterialType.mat4:
        return "MAT4";
      case MaterialType.texture:
        return "TEXTURE";
    }
  }
}
