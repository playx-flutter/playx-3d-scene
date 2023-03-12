import 'package:playx_3d_scene/models/scene/camera/camera.dart';
import 'package:playx_3d_scene/models/scene/ground.dart';
import 'package:playx_3d_scene/models/scene/indirect_light/indirect_light.dart';
import 'package:playx_3d_scene/models/scene/light/light.dart';
import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';

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
