import 'package:playx_3d_scene/models/scene/camera.dart';
import 'package:playx_3d_scene/models/scene/ground.dart';
import 'package:playx_3d_scene/models/scene/indirect_light/indirect_light.dart';
import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';

class Scene {
  Skybox? skybox;
  IndirectLight? indirectLight;
  Camera? camera;
  Ground? ground;

  Scene({this.skybox, this.indirectLight, this.camera, this.ground});

  Map<String, dynamic> toJson() => {
        'skybox': skybox?.toJson(),
        'indirectLight': indirectLight?.toJson(),
        'camera': camera?.toJson(),
        'ground': ground?.toJson(),
      };
}
