import 'dart:ui';

extension ColorsExt on Color {
  String toHex() {
    return "#${value.toRadixString(16)}";
  }
}
