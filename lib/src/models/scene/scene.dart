import 'package:playx_3d_scene/src/models/scene/camera/camera.dart';
import 'package:playx_3d_scene/src/models/scene/ground.dart';
import 'package:playx_3d_scene/src/models/scene/indirect_light/indirect_light.dart';
import 'package:playx_3d_scene/src/models/scene/light/light.dart';
import 'package:playx_3d_scene/src/models/scene/skybox/skybox.dart';

/// An object that represents the scene to  be rendered with information about light, skybox and more.
class Scene {
  Skybox? skybox;
  IndirectLight? indirectLight;
  Light? light;
  Camera? camera;
  Ground? ground;

  Scene(
      {this.skybox, this.indirectLight, this.light, this.camera, this.ground});

  Map<String, dynamic> toJson() => {
        'skybox': skybox?.toJson(),
        'light': light?.toJson(),
        'indirectLight': indirectLight?.toJson(),
        'camera': camera?.toJson(),
        'ground': ground?.toJson(),
      };

  @override
  String toString() {
    return 'Scene(skybox: $skybox, indirectLight: $indirectLight, light: $light, camera: $camera, ground: $ground)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is Scene &&
        other.skybox == skybox &&
        other.indirectLight == indirectLight &&
        other.light == light &&
        other.camera == camera &&
        other.ground == ground;
  }

  @override
  int get hashCode {
    return skybox.hashCode ^
        indirectLight.hashCode ^
        light.hashCode ^
        camera.hashCode ^
        ground.hashCode;
  }

  Scene copyWith({
    Skybox? skybox,
    IndirectLight? indirectLight,
    Light? light,
    Camera? camera,
    Ground? ground,
  }) {
    return Scene(
      skybox: skybox ?? this.skybox,
      indirectLight: indirectLight ?? this.indirectLight,
      light: light ?? this.light,
      camera: camera ?? this.camera,
      ground: ground ?? this.ground,
    );
  }
}
