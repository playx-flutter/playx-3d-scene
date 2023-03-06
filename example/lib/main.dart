import 'package:fimber/fimber.dart';
import 'package:flutter/material.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/models/model/animation.dart';
import 'package:playx_3d_scene/models/model/glb_model.dart';
import 'package:playx_3d_scene/models/scene/scene.dart';
import 'package:playx_3d_scene/models/scene/skybox.dart';
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
                model: GlbModel.url(
                  "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/DragonAttenuation/glTF-Binary/DragonAttenuation.glb11",
                  fallback: GlbModel.asset("assets/models/Fox.glb",
                      animation: PlayxAnimation.byIndex(0, autoPlay: true)),
                  animation: PlayxAnimation.byIndex(1, autoPlay: true),
                ),
                scene: Scene(skybox: Skybox.color(Colors.yellow)),
                onCreated: (Playx3dSceneController controller) {
                  Fimber.d("My Playx3dScenePlugin onCreated");
                },
                onModelLoadingStateChanged: (isLoading) {
                  setState(() {
                    Fimber.d(
                        "My Playx3dScenePlugin isModelLoading : $isLoading");
                    isModelLoading = isLoading;
                  });
                },
              ),
              isModelLoading
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
