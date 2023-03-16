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
