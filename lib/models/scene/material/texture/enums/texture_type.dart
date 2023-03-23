///Type of the texture to be used
/// Color is the only type of texture we want to pre-multiply with the alpha channel
/// Pre-multiplication is the default behavior, so we need to turn it off  based on the type.
enum TextureType {
  color,
  normal,
  data;

  String toName() {
    switch (this) {
      case TextureType.color:
        return "COLOR";
      case TextureType.normal:
        return "NORMAL";
      case TextureType.data:
        return "DATA";
    }
  }
}
