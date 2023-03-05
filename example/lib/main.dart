import 'package:flutter/material.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
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
              glbAssetPath: "assets/models/Fox.glb",
              autoPlay: true,
              animationIndex: 0,
              onCreated: (Playx3dSceneController controller) {
                controller.changeAnimationByIndex(2);
              },
            ),
          ),
        ),
      ),
    );
  }
}
