import 'package:flutter/material.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/models/model/animation.dart';
import 'package:playx_3d_scene/models/model/glb_model.dart';
import 'package:playx_3d_scene/models/scene/scene.dart';
import 'package:playx_3d_scene/models/scene/skybox.dart';
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
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Container(
            color: Colors.cyan,
            child: Playx3dScene(
              model: GlbModel.asset(
                "assets/models/Fox.glb",
                animation: PlayxAnimation.byIndex(1, autoPlay: true),
              ),
              scene: Scene(skybox: Skybox.color(Colors.green)),
              onCreated: (Playx3dSceneController controller) {
                // controller.changeAnimationByIndex(2);
              },
            ),
          ),
        ),
      ),
    );
  }
}
