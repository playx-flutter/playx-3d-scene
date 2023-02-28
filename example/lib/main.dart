import 'package:flutter/material.dart';
import 'package:playx_model_viewer/controller/playx_model_viewer_controller.dart';
import 'package:playx_model_viewer/view/playx_model_viewer.dart';

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
            color: Colors.yellow,
            child: PlayXModelViewer(
              glbAssetPath: "assets/models/Fox.glb",
              environmentAssetPath:
                  "assets/envs/venetian_crossroads_2k/venetian_crossroads_2k_skybox.ktx",
              onCreated: (PlayXModelViewerController controller) {},
              autoPlay: true,
              animationIndex: 0,
              // lightIntensity: 30000.0,
              // gltfAssetPath: "assets/models/BusterDrone.gltf",
              //   gltfImagePathPrefix: "assets/models/",
              // environmentColor: Colors.white,
              //  lightIntensity: 50000.0,
              // lightAssetPath:
              // "assets/envs/venetian_crossroads_2k/venetian_crossroads_2k_ibl.ktx",
            ),
          ),
        ),
      ),
    );
  }
}
