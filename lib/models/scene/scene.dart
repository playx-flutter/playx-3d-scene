import 'package:playx_3d_scene/models/scene/camera.dart';
import 'package:playx_3d_scene/models/scene/ground.dart';
import 'package:playx_3d_scene/models/scene/light.dart';
import 'package:playx_3d_scene/models/scene/skybox.dart';

class Scene {
  Skybox? skybox;
  Light? light;
  Camera? camera;
  Ground? ground;

  Scene({this.skybox, this.light, this.camera, this.ground});

  Map<String, dynamic> toJson() => {
        'skybox': skybox?.toJson(),
        'light': light?.toJson(),
        'camera': camera?.toJson(),
        'ground': ground?.toJson(),
      };
}
