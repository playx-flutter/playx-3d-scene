import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:playx_3d_scene/models/model/glb_model.dart';
import 'package:playx_3d_scene/models/scene/scene.dart';
import 'package:playx_3d_scene/models/scene/skybox/hdr_skybox.dart';
import 'package:playx_3d_scene/models/scene/skybox/skybox.dart';
import 'package:playx_3d_scene/view/playx_3d_scene.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  MyApp({super.key});

  List<GlbModel> models = [
    GlbModel.asset(
      'assets/models/shoe1.glb',
    ),
    GlbModel.asset(
      'assets/models/shoe2.glb',
    ),
    GlbModel.asset('assets/models/chair.glb'),
    GlbModel.asset('assets/models/lotion.glb'),
    GlbModel.asset('assets/models/shoe3.glb'),
    GlbModel.asset('assets/models/shoe4.glb'),
    GlbModel.asset('assets/models/shoe1.glb'),
    GlbModel.asset('assets/models/shoe2.glb'),
    GlbModel.asset('assets/models/chair.glb'),
    GlbModel.asset('assets/models/lotion.glb'),
    GlbModel.asset('assets/models/shoe3.glb'),
    GlbModel.asset('assets/models/shoe4.glb'),
  ];

  List<Skybox> skyboxes = [
    HdrSkybox.asset("assets/envs/courtyard.hdr"),
    HdrSkybox.asset("assets/envs/field.hdr"),
    HdrSkybox.asset("assets/envs/sky.hdr"),
    HdrSkybox.asset("assets/envs/courtyard.hdr"),
    HdrSkybox.asset("assets/envs/field.hdr"),
    HdrSkybox.asset("assets/envs/sky.hdr"),
    HdrSkybox.asset("assets/envs/courtyard.hdr"),
    HdrSkybox.asset("assets/envs/field.hdr"),
    HdrSkybox.asset("assets/envs/sky.hdr"),
    HdrSkybox.asset("assets/envs/courtyard.hdr"),
    HdrSkybox.asset("assets/envs/field.hdr"),
    HdrSkybox.asset("assets/envs/sky.hdr"),
  ];

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          body: GridView.builder(
        itemCount: models.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2, childAspectRatio: .7),
        itemBuilder: (BuildContext context, int index) {
          return Card(
            elevation: 8,
            margin: const EdgeInsets.all(20),
            child: SizedBox(
              height: 300,
              child: Playx3dScene(
                model: models[index],
                gestureRecognizers: <Factory<OneSequenceGestureRecognizer>>{
                  Factory<OneSequenceGestureRecognizer>(
                    () => EagerGestureRecognizer(),
                  ),
                },
                scene: Scene(skybox: skyboxes[index]),
              ),
            ),
          );
        },
      )),
    );
  }
}
