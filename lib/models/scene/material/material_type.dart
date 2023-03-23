///material types base on filamant general parameters.
///See also:
///* https://google.github.io/filament/Materials.html
enum MaterialType {
  /// Material value presented as color.
  color,

  /// Single boolean or Vector of 2 to 4 booleans
  ///used for material bool type, bool2,bool3,bool4 types
  /// Material value presented as bool.
  bool,

  /// Material value presented as Vector of 2 to 4 booleans.
  boolVector,

  /// Material value presented as float.
  float,

  /// Material value presented as Vector of 2 to 4 booleans.
  floatVector,

  /// Material value presented as int.
  int,

  /// Material value presented as Vector of 2 to 4 booleans.
  intVector,

  /// Material value presented as 3x3 matrix.
  mat3,

  /// Material value presented as 4x4 matrix.
  mat4,

  /// Material value presented as texture.
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
