import 'package:fimber/fimber.dart';
import 'package:flutter/material.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/models/model/animation.dart';
import 'package:playx_3d_scene/models/model/glb_model.dart';
import 'package:playx_3d_scene/models/scene/camera/camera.dart';
import 'package:playx_3d_scene/models/scene/camera/exposure.dart';
import 'package:playx_3d_scene/models/scene/geometry/direction.dart';
import 'package:playx_3d_scene/models/scene/geometry/size.dart';
import 'package:playx_3d_scene/models/scene/ground.dart';
import 'package:playx_3d_scene/models/scene/indirect_light/hdr_indirect_light.dart';
import 'package:playx_3d_scene/models/scene/light/light.dart';
import 'package:playx_3d_scene/models/scene/light/light_type.dart';
import 'package:playx_3d_scene/models/scene/material/material.dart';
import 'package:playx_3d_scene/models/scene/material/material_paramater.dart';
import 'package:playx_3d_scene/models/scene/material/texture/enums/texture_type.dart';
import 'package:playx_3d_scene/models/scene/material/texture/texture.dart';
import 'package:playx_3d_scene/models/scene/material/texture/texture_sampler.dart';
import 'package:playx_3d_scene/models/scene/scene.dart';
import 'package:playx_3d_scene/models/scene/skybox/hdr_skybox.dart';
import 'package:playx_3d_scene/models/state/model_state.dart';
import 'package:playx_3d_scene/models/state/scene_state.dart';
import 'package:playx_3d_scene/view/playx_3d_scene.dart';

void main() {
  Fimber.plantTree(DebugTree());
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool isModelLoading = false;
  bool isSceneLoading = false;
  late Playx3dSceneController controller;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Stack(
            children: [
              Playx3dScene(
                model: GlbModel.asset("assets/models/Fox.glb",
                    animation: PlayxAnimation.byIndex(0, autoPlay: true),
                    fallback: GlbModel.asset("assets/models/Fox.glb"),
                    centerPosition: [0, 0, -4]),
                scene: Scene(
                  skybox: HdrSkybox.asset("assets/envs/courtyard.hdr"),
                  indirectLight:
                      HdrIndirectLight.asset("assets/envs/courtyard.hdr"),
                  light: Light(
                    type: LightType.directional,
                    colorTemperature: 6500,
                    intensity: 10000,
                    castShadows: false,
                    castLight: true,
                  ),
                  ground: Ground(
                    size: PlayxSize(width: 4.0, height: 4.0),
                    isBelowModel: true,
                    normal: PlayxDirection.y(1.0),
                    material: PlayxMaterial.asset(
                      "assets/materials/textured_pbr.filamat",
                      parameters: [
                        MaterialParameter.texture(
                          value: PlayxTexture.asset(
                            "assets/materials/texture/floor_basecolor.png",
                            type: TextureType.color,
                            sampler: PlayxTextureSampler(anisotropy: 8),
                          ),
                          name: "baseColor",
                        ),
                        MaterialParameter.texture(
                          value: PlayxTexture.asset(
                            "assets/materials/texture/floor_normal.png",
                            type: TextureType.normal,
                            sampler: PlayxTextureSampler(anisotropy: 8),
                          ),
                          name: "normal",
                        ),
                        MaterialParameter.texture(
                          value: PlayxTexture.asset(
                            "assets/materials/texture/floor_ao_roughness_metallic.png",
                            type: TextureType.data,
                            sampler: PlayxTextureSampler(anisotropy: 8),
                          ),
                          name: "aoRoughnessMetallic",
                        ),
                      ],
                    ),
                  ),
                  camera: Camera.orbit(
                    exposure: Exposure.formAperture(
                      aperture: 16.0,
                      shutterSpeed: 1 / 125,
                      sensitivity: 150,
                    ),
                    targetPosition: [0.0, 0, -4.0],
                    orbitHomePosition: [0.0, 1, 1],
                    upVector: [0, 1, 0],
                  ),
                ),
                onCreated: (Playx3dSceneController controller) async {
                  await Future.delayed(const Duration(seconds: 5), () {
                    controller.updateGroundMaterial(PlayxMaterial.asset(
                      "assets/materials/lit.filamat",
                      parameters: [
                        MaterialParameter.baseColor(color: Colors.cyan)
                      ],
                    ));
                  });
                },
                onModelStateChanged: (state) {
                  Fimber.d(
                      "My Playx3dScenePlugin onModelStateChanged : $state");
                  setState(() {
                    isModelLoading = state == ModelState.loading;
                  });
                },
                onSceneStateChanged: (state) {
                  setState(() {
                    isSceneLoading = state == SceneState.loading;
                  });
                  Fimber.d(
                      "My Playx3dScenePlugin onSceneStateChanged : $state");
                },
                onEachRender: (frameTimeNano) {},
              ),
              isModelLoading || isSceneLoading
                  ? const Center(
                      child: CircularProgressIndicator(
                        color: Colors.pink,
                      ),
                    )
                  : Container(),
            ],
          ),
        ),
      ),
    );
  }
}
