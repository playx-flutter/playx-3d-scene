import 'package:fimber/fimber.dart';
import 'package:flutter/material.dart';
import 'package:playx_3d_scene/controller/playx_3d_scene_controller.dart';
import 'package:playx_3d_scene/models/model/animation.dart';
import 'package:playx_3d_scene/models/model/glb_model.dart';
import 'package:playx_3d_scene/models/scene/scene.dart';
import 'package:playx_3d_scene/models/scene/skybox.dart';
import 'package:playx_3d_scene/models/state/model_state.dart';
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
                model: GlbModel.asset("assets/models/Fox.glb",
                    animation: PlayxAnimation.byIndex(0, autoPlay: true)),
                scene: Scene(skybox: Skybox.asset("assets/envs/courtyard.hdr")),
                //   skybox: Skybox.url(
                //       "https://dl.dropbox.com/s/zyxt8bnnddhrywb/field2.hdr"),
                // ),
                onCreated: (Playx3dSceneController controller) {
                  Fimber.d("My Playx3dScenePlugin onCreated");
                },
                onModelStateChanged: (state) {
                  Fimber.d(
                      "My Playx3dScenePlugin onModelStateChanged : $state");
                  setState(() {
                    isModelLoading = state == ModelState.loading;
                  });
                },
                onSceneStateChanged: (state) {
                  Fimber.d(
                      "My Playx3dScenePlugin onSceneStateChanged : $state");
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
