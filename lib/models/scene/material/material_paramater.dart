import 'dart:ui';

import 'package:playx_3d_scene/models/scene/material/material_type.dart';
import 'package:playx_3d_scene/models/scene/material/texture/texture.dart';
import 'package:playx_3d_scene/utils/utils.dart';

/// An object that represents material parameters that are defined in the .mat file.
class MaterialParameter {
  /// Name of the material parameter defined in the .mat file
  String name;

  /// value of the material parameter.
  dynamic value;

  /// type of the material parameter.
  MaterialType type = MaterialType.float;

  MaterialParameter._({required this.name, required this.value});

  /// create a material parameter of color type.
  MaterialParameter.color({required Color color, required this.name}) {
    value = color.toHex();
    type = MaterialType.color;
  }

  /// create a material parameter of float type.
  MaterialParameter.float({required double this.value, required this.name}) {
    type = MaterialType.float;
  }

  /// create a material parameter of float Vector type.
  /// It takes list of max 4 double elements as parameter.
  MaterialParameter.floatVector({
    required List<double> this.value,
    required this.name,
  }) {
    type = MaterialType.floatVector;
  }

  /// create a material parameter of bool type.
  MaterialParameter.bool({required bool this.value, required this.name}) {
    type = MaterialType.bool;
  }

  /// create a material parameter of bool vector type.
  /// It takes list of max 4 bool elements as parameter.
  MaterialParameter.boolVector({
    required List<bool> this.value,
    required this.name,
  }) {
    type = MaterialType.boolVector;
  }

  /// create a material parameter of int type.
  MaterialParameter.int({
    required num this.value,
    required this.name,
  }) {
    type = MaterialType.int;
  }

  /// create a material parameter of int vector type.
  /// It takes list of max 4 int elements as parameter.
  MaterialParameter.intVector({
    required List<num> this.value,
    required this.name,
  }) {
    type = MaterialType.intVector;
  }

  /// create a material parameter of 3x3 matrix type.
  ///It takes list of 9 double elements as parameter.
  MaterialParameter.mat3({
    required List<double> this.value,
    required this.name,
  }) {
    type = MaterialType.mat3;
  }

  /// create a material parameter of 4x4 matrix type.
  ///It takes list of 16 double elements as parameter.
  MaterialParameter.mat4({
    required List<double> this.value,
    required this.name,
  }) {
    type = MaterialType.mat4;
  }

  /// create a material parameter of texture type.
  MaterialParameter.texture({
    required PlayxTexture? this.value,
    required this.name,
  }) {
    type = MaterialType.texture;
  }

  /// create a material parameter of color type with baseColor parameter name.
  MaterialParameter.baseColor({required Color color, this.name = 'baseColor'}) {
    value = color.toHex();
    type = MaterialType.color;
  }

  /// create a material parameter of float type with metallic parameter name.
  MaterialParameter.metallic(
      {required double this.value, this.name = "metallic"}) {
    type = MaterialType.float;
  }

  /// create a material parameter of float type with roughness parameter name.
  MaterialParameter.roughness(
      {required double this.value, this.name = "roughness"}) {
    type = MaterialType.float;
  }

  Map<String, dynamic> toJson() {
    dynamic valueJson;
    if (type == MaterialType.texture) {
      if (value is PlayxTexture) {
        valueJson = (value as PlayxTexture?)?.toJson();
      }
    } else {
      valueJson = value;
    }
    return {
      'name': name,
      'value': valueJson,
      'type': type.toName(),
    };
  }
}
