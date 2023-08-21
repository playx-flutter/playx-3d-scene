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
}
