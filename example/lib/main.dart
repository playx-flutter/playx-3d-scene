import 'package:flutter/material.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/models/model/animation.dart';
import 'package:playx_3d_scene/models/model/glb_model.dart';
import 'package:playx_3d_scene/models/scene/camera/camera.dart';
import 'package:playx_3d_scene/models/scene/camera/exposure.dart';
import 'package:playx_3d_scene/models/scene/geometry/direction.dart';
import 'package:playx_3d_scene/models/scene/geometry/position.dart';
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
import 'package:playx_3d_scene/models/shapes/cube.dart';
import 'package:playx_3d_scene/models/shapes/sphere.dart';
import 'package:playx_3d_scene/models/state/model_state.dart';
import 'package:playx_3d_scene/models/state/scene_state.dart';
import 'package:playx_3d_scene/models/state/shape_state.dart';
import 'package:playx_3d_scene/utils/result.dart';
import 'package:playx_3d_scene/view/playx_3d_scene.dart';

void main() {
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
  bool isShapeLoading = false;
  late Playx3dSceneController controller;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Stack(
          children: [
            Playx3dScene(
              model: GlbModel.asset(
                "assets/models/Fox.glb",
                animation: PlayxAnimation.byIndex(0, autoPlay: true),
                fallback: GlbModel.asset("assets/models/Fox.glb"),
                centerPosition: PlayxPosition(x: 0, y: 0, z: -4),
                scale: 1.0,
              ),
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
                    position: PlayxPosition(x: -1, y: 0, z: 0),
                    direction: PlayxDirection(x: -1, y: -1, z: 0)),
                ground: Ground(
                  width: 4.0,
                  height: 4.0,
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
                  targetPosition: PlayxPosition(x: 0.0, y: 0.0, z: -4.0),
                  orbitHomePosition: PlayxPosition(x: 0.0, y: 1.0, z: 1.0),
                  upVector: PlayxPosition(x: 0.0, y: 1.0, z: 0.0),
                ),
              ),
              shapes: [
                Cube(
                  id: 1,
                  length: .5,
                  centerPosition: PlayxPosition(x: -1, y: 0, z: -4),
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
                Sphere(
                  id: 2,
                  centerPosition: PlayxPosition(x: 1, y: 0, z: -4),
                  radius: .5,
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
                )
              ],
              onCreated: (Playx3dSceneController controller) async {
                Future.delayed(const Duration(seconds: 5), () async {
                  Result<int?> result =
                      await controller.changeAnimationByIndex(1);

                  if (result.isSuccess()) {
                    final data = result.data;
                    print("success :$data");
                  } else {
                    print(result.message);
                  }
                });
              },
              onModelStateChanged: (state) {
                setState(() {
                  isModelLoading = state == ModelState.loading;
                });
              },
              onSceneStateChanged: (state) {
                setState(() {
                  isSceneLoading = state == SceneState.loading;
                });
              },
              onShapeStateChanged: (state) {
                setState(() {
                  isShapeLoading = state == ShapeState.loading;
                });
              },
              onEachRender: (frameTimeNano) {},
            ),
            isModelLoading || isSceneLoading || isShapeLoading
                ? const Center(
                    child: CircularProgressIndicator(
                      color: Colors.pink,
                    ),
                  )
                : Container(),
          ],
        ),
      ),
    );
  }
}
