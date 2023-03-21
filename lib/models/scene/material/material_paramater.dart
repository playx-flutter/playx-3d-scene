import 'dart:ui';

import 'package:playx_3d_scene/models/scene/material/material_type.dart';
import 'package:playx_3d_scene/models/scene/material/texture/texture.dart';
import 'package:playx_3d_scene/utils/utils.dart';

class MaterialParameter {
  String name;
  dynamic value;
  MaterialType type = MaterialType.float;

  MaterialParameter._({required this.name, required this.value});

  MaterialParameter.color({required Color color, required this.name}) {
    value = color.toHex();
    type = MaterialType.color;
  }

  MaterialParameter.float({required double this.value, required this.name}) {
    type = MaterialType.float;
  }

  MaterialParameter.floatVector({
    required List<double> this.value,
    required this.name,
  }) {
    type = MaterialType.floatVector;
  }

  MaterialParameter.bool({required bool this.value, required this.name}) {
    type = MaterialType.bool;
  }

  MaterialParameter.boolVector({
    required List<bool> this.value,
    required this.name,
  }) {
    type = MaterialType.boolVector;
  }

  MaterialParameter.int({
    required num this.value,
    required this.name,
  }) {
    type = MaterialType.int;
  }

  MaterialParameter.intVector({
    required List<num> this.value,
    required this.name,
  }) {
    type = MaterialType.intVector;
  }

  MaterialParameter.mat3({
    required List<double> this.value,
    required this.name,
  }) {
    type = MaterialType.mat3;
  }

  MaterialParameter.mat4({
    required List<double> this.value,
    required this.name,
  }) {
    type = MaterialType.mat4;
  }
  MaterialParameter.texture({
    required PlayxTexture? this.value,
    required this.name,
  }) {
    type = MaterialType.texture;
  }
  MaterialParameter.baseColor({required Color color, this.name = 'baseColor'}) {
    value = color.toHex();
    type = MaterialType.color;
  }

  MaterialParameter.metallic(
      {required double this.value, this.name = "metallic"}) {
    type = MaterialType.float;
  }

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
