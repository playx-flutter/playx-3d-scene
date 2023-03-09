import 'package:fimber/fimber.dart';
import 'package:flutter/material.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/models/model/animation.dart';
import 'package:playx_3d_scene/models/model/glb_model.dart';
import 'package:playx_3d_scene/models/scene/indirect_light/hdr_indirect_light.dart';
import 'package:playx_3d_scene/models/scene/light/light.dart';
import 'package:playx_3d_scene/models/scene/light/light_type.dart';
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
                model: GlbModel.asset(
                  "assets/models/Fox.glb",
                  animation: PlayxAnimation.byIndex(
                    0,
                  ),
                ),
                scene: Scene(
                  skybox: HdrSkybox.asset("assets/envs/courtyard.hdr"),
                  indirectLight:
                      HdrIndirectLight.asset("assets/envs/field2.hdr"),
                  light: Light(
                    type: LightType.directional,
                    colorTemperature: 6500,
                    intensity: 10000,
                    castShadows: true,
                    castLight: true,
                  ),
                ),
                onCreated: (Playx3dSceneController controller) {
                  Fimber.d("My Playx3dScenePlugin onCreated");
                  this.controller = controller;
                  controller.changeSceneLight(
                    Light(
                      type: LightType.directional,
                      color: Colors.white,
                      intensity: 100000,
                      direction: [0, -1, 0],
                      position: [0, 0, 0],
                      castShadows: true,
                      castLight: true,
                    ),
                  );
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
